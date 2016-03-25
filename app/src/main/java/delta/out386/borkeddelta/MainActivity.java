package delta.out386.borkeddelta;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;

import org.apache.commons.io.IOUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import eu.chainfire.libsuperuser.*;

public class MainActivity extends Activity
implements NavigationDrawerFragment.NavigationDrawerCallbacks
{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #//restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState)
	{
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
		new extractAssets(getFilesDir().toString(),this).execute();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
			getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));


    }

    @Override
    public void onNavigationDrawerItemSelected(int position)
	{
        // update the main content by replacing fragments
        Fragment fragment=new Fragment();
        switch (position)
        {
            case 0: fragment = BaseFragment.newInstance(position + 1);
				break;
         //   case 1: fragment = PlaceholderFragment.newInstance(position + 1);
				//break;
        }
        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction()
			.replace(R.id.container, fragment)
			.commit();
    }

    public void onSectionAttached(int number)
	{
        switch (number)
		{
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

	/*public void restoreActionBar() {
	 ActionBar actionBar = getActionBar();
	 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	 actionBar.setDisplayShowTitleEnabled(true);
	 actionBar.setTitle(mTitle);
	 }*/


    /*@Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	 if (!mNavigationDrawerFragment.isDrawerOpen()) {
	 // Only show items in the action bar relevant to this screen
	 // if the drawer is not showing. Otherwise, let the drawer
	 // decide what to show in the action bar.
	 getMenuInflater().inflate(R.menu.main, menu);
	 //restoreActionBar();
	 return true;
	 }
	 return super.onCreateOptionsMenu(menu);
	 }*/

    /*@Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	 // Handle action bar item clicks here. The action bar will
	 // automatically handle clicks on the Home/Up button, so long
	 // as you specify a parent activity in AndroidManifest.xml.
	 int id = item.getItemId();

	 //noinspection SimplifiableIfStatement
	 if (id == R.id.action_settings) {
	 return true;
	 }

	 return super.onOptionsItemSelected(item);
	 }*/

    /**
     * A placeholder fragment containing a simple view.
     */
    

    public void extractXdelta(View view)
	{
        InputStream is = null;
        OutputStream os = null;
        String storage_dir = getFilesDir().toString();
        File deltaFile = new File(storage_dir + "/xdelta");
        if (deltaFile.exists())
            deltaFile.setWritable(true, true);
        else
		{
			try
			{
				is = getAssets().open("xdelta3");
				os = new FileOutputStream(deltaFile);
				IOUtils.copy(is, os);
				is.close();
				os.flush();
				os.close();
				is = null;
				os = null;
			}
			catch (IOException e)
			{
				Log.e("borked", e.toString());
			}
        }
        Shell.SU.run("chmod 777 " + storage_dir + "/xdelta");
        List < String > outputList =  Shell.SU.run(storage_dir + "/xdelta -d -s /sdcard/zzbase.zip /sdcard/zzdelta /sdcard/zzdone"
												   );
        //TextView outputTV=(TextView)findViewById(R.id.textView);
        //outputTV.setText(outputList.toString());
    }
	
	
}
