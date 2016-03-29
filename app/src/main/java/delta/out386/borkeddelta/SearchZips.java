package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.tjerkw.slideexpandable.library.SlideExpandableListAdapter;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.Collection;
import java.util.List;

public class SearchZips extends AsyncTask<Void, Void,FlashablesTypeList > {

    Context context;
    boolean isReload=false;
    LoadingDialogFragment loading = new LoadingDialogFragment();
    public SearchZips(Context cont, boolean isReload){
        this.isReload = isReload;
        context = cont;
    }

    @Override
    protected void onPreExecute(){
        Activity activity = (Activity) context;
        loading.setCancelable(false);
        try {
            loading.show(activity.getFragmentManager(), "dialog");
        }
        catch(ClassCastException e) {
            Log.e("borked", e.toString());
        }

    }
    @Override
    protected FlashablesTypeList doInBackground(Void... params){
        FlashablesTypeList output = null;
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/UCDownloads");
        File f = new File(context.getFilesDir().toString() + "/FlashablesTypeList");
        if(!f.exists() || isReload) {
            Collection zipsCollection;
            if (directory.exists()) {
                zipsCollection = FileUtils.listFiles(directory, new String[]{"zip"}, false);
                output = new FilesCategorize().run(zipsCollection, context);
                ObjectOutputStream oos = null;
                if(output != null) {
                    try {
                        oos = new ObjectOutputStream(new FileOutputStream(f));
                        oos.writeObject(output);
                        oos.close();
                    } catch (FileNotFoundException e) {
                        Log.e("borked", e.toString());
                    } catch (IOException e) {
                        Log.e("borked", e.toString());
                    }
                    return output;
                }
            }
        }
        else {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                output = (FlashablesTypeList) ois.readObject();
            }
            catch(FileNotFoundException e) {
                Log.e("borked", e.toString());
            }
            catch(StreamCorruptedException e) {
                Log.e("borked", e.toString());
            }
            catch(IOException e) {
                Log.e("borked", e.toString());
            }
            catch(ClassNotFoundException e) {
                Log.e("borked", e.toString());
            }
        }
         return output;
    }

    @Override
    protected void onPostExecute(FlashablesTypeList output){

        /*ListView lv=(ListView) rootView.findViewById(R.id.listView);
        FlashablesAdapter adapter = new FlashablesAdapter(context,
                R.layout.list_item, output);

        // Assign adapter to ListView
        lv.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.list_normal_view,
                        R.id.list_expandable_view)
        );
*/
        loading.dismiss();
    }
}