package delta.out386.xosp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.content.Intent;
import java.util.List;
import android.support.v4.widget.DrawerLayout;

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

        List<String> xospVersion = Shell.SH.run("getprop ro.xosp.version");
        if(xospVersion == null || xospVersion.size() == 0) {
            Intent notXospDialog = new Intent(Constants.ACTION_NOT_XOSP_DIALOG);
            startActivity(new Intent(this, DeltaDialogActivity.class)
               .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        /* Delay needed as the dialog activity needs time to register
          * the broadcast receiver
          */
        try {
            Thread.sleep(90);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
            sendBroadcast(notXospDialog);
            onDestroy();
        }
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
            case 1: fragment = BaseFragment.newInstance(position + 1);
                break;
            case 2: fragment = BaseFragment.newInstance(position + 1);
                break;
            case 3: fragment = BaseFragment.newInstance(position + 1);
                break;
            case 4: fragment = QueueFragment.newInstance(position + 1);
                break;
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
            case 4:
                mTitle = getString(R.string.title_section4);
                break;
        }
    }

	public void restoreActionBar() {
	 ActionBar actionBar = getActionBar();
	 actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	 actionBar.setDisplayShowTitleEnabled(true);
	 actionBar.setTitle(mTitle);
	 }


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
    @Override
    public void onRequestPermissionsResult(int code, String[] permissions, int[] result) {
        if(result.length > 0 && result[0] == PackageManager.PERMISSION_GRANTED)
        {
            FragmentManager fragmentManager = getFragmentManager();
            BaseFragment fragment = BaseFragment.newInstance(1);
            View mFragmentContainerView = findViewById(R.id.navigation_drawer);
            DrawerLayout mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
            mDrawerLayout.closeDrawer(mFragmentContainerView);
            fragmentManager.beginTransaction()
                    .replace(R.id.container, fragment)
                    .commit();
        }
    }
}
