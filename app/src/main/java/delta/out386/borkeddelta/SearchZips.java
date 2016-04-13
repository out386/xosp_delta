package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.cjj.MaterialRefreshLayout;
import com.cjj.MaterialRefreshListener;
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
    boolean isReload=false, isLoading = false;
    View rootView;
    String typeToDisplay = "roms";
    final String TAG = Constants.TAG;
    File f = null;
    MaterialRefreshLayout refresh;
    LoadingDialogFragment loading = new LoadingDialogFragment(R.layout.fragment_loading_dialog);
    public SearchZips(Context context, boolean isReload, View rootView, String typeToDisplay, MaterialRefreshLayout refresh){
        this.isReload = isReload;
        this.typeToDisplay = typeToDisplay;
        this.rootView = rootView;
        this.refresh = refresh;
        this.context = context;
    }

    @Override
    protected void onPreExecute(){
        f = new File(context.getFilesDir().toString() + "/FlashablesTypeList");
        if(!isReload && !f.exists()) {
            isLoading = true;
            Activity activity = (Activity) context;
            loading.setCancelable(false);
            try {
                loading.show(activity.getFragmentManager(), "dialog");
            } catch (ClassCastException e) {
                Log.e(TAG, e.toString());
            }
        }

    }
    @Override
    protected FlashablesTypeList doInBackground(Void... params){
        FlashablesTypeList output = null;
        File directory = null;
        boolean directoryExists = true;
        try {
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/borkeddelta");
                if (!directory.exists())
                    directoryExists = directory.mkdir();
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
        if(!directoryExists || directory == null) {
            Log.e(TAG, "Couldn't create storage directory");
            return null;
        }
        if(!f.exists() || isReload) {
            Collection<File> zipsCollection;
            if (f.exists())
                f.delete();
            zipsCollection = FileUtils.listFiles(directory, new String[]{"zip"}, false);
            output = new FilesCategorize().run(zipsCollection, context);
            ObjectOutputStream oos;
            if(output != null) {
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(f));
                    oos.writeObject(output);
                    oos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                refreshDone();
                return output;
            }
        }
        else {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                output = (FlashablesTypeList) ois.readObject();
                ois.close();
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
                refreshDone();
            }
        }
        refreshDone();
         return output;
    }

    @Override
    protected void onPostExecute(FlashablesTypeList output){

        final MaterialRefreshLayout refresh = (MaterialRefreshLayout)rootView.findViewById(R.id.refresh);
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                new SearchZips(context, true, rootView, typeToDisplay, refresh).execute();
            }
        });
        if(output == null)
            return;

        boolean isEmpty = true;
        ListView lv=(ListView) rootView.findViewById(R.id.listView);
        FlashablesAdapter adapter = new FlashablesAdapter(context,
                R.layout.list_item, output.roms);

        if(typeToDisplay.equals("roms")) {
            if(output.roms.size() != 0)
                isEmpty = false;
        }
        if(typeToDisplay.equals("kernels")) {
            if(output.kernels.size() != 0)
                    isEmpty = false;
            adapter = new FlashablesAdapter(context,
                    R.layout.list_item, output.kernels);
        }
        if(typeToDisplay.equals("deltas")) {
            if(output.deltas.size() != 0)
                isEmpty = false;
            adapter = new FlashablesAdapter(context,
                    R.layout.list_item, output.deltas);
        }
        if(typeToDisplay.equals("others")) {
            if(output.others.size() != 0)
                isEmpty = false;
            adapter = new FlashablesAdapter(context,
                    R.layout.list_item, output.others);
        }
        if(isEmpty)
        {
            RelativeLayout baseEmpty = (RelativeLayout) rootView.findViewById(R.id.baseEmptyLayout);
            baseEmpty.setVisibility(View.VISIBLE);
            if(isLoading)
                loading.dismiss();
            return;
        }
        lv.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.list_normal_view,
                        R.id.list_expandable_view)
        );

        if(isLoading)
            loading.dismiss();
    }
    
    public void refreshDone()
    {
        if(refresh == null)
            return;
        try {
            // To display the animation even if the reload happens fast, removes stutters
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
        refresh.finishRefresh();
    }
}