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
import java.util.Collection;
import java.util.List;

public class SearchZips extends AsyncTask<Void, Void,List<Flashables> > {

    Context context;
    View rootView;
    LoadingDialogFragment loading = new LoadingDialogFragment();
    public SearchZips(Context cont,View rootView){
        this.rootView = rootView;
        context=cont;
    }

    @Override
    protected void onPreExecute(){
            /*TextView outputTV = (TextView) rootView.findViewById(R.id.textView);
            outputTV.setText("working");*/
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
    protected List<Flashables> doInBackground(Void... params){
        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/UCDownloads");
        Collection zipsCollection = FileUtils.listFiles(directory, new String[]{"zip"}, false);
        return new FilesCategorize().run(zipsCollection, rootView, context);
        // return zipsCollection;
    }

    @Override
    protected void onPostExecute(List<Flashables> output){
			/*TextView out = (TextView) rootView.findViewById(R.id.textView);
			out.setText(output);*/
        ListView lv=(ListView) rootView.findViewById(R.id.listView);
        FlashablesAdapter adapter = new FlashablesAdapter(context,
                R.layout.list_item, output);

        // Assign adapter to ListView
        lv.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.list_normal_view,
                        R.id.list_expandable_view)
        );

        loading.dismiss();
    }
}