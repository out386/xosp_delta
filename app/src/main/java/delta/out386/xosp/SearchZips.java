package delta.out386.xosp;

import android.content.Context;
import android.content.Intent;
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
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class SearchZips extends AsyncTask<Void, Void,FlashablesTypeList > {

    Context context;
    boolean isReload=false, isLoading = false;
    View rootView;
    String typeToDisplay = "roms";
    MaterialRefreshLayout refresh;
    Intent closeDialog = new Intent(Constants.ACTION_CLOSE_DIALOG);
    public SearchZips(Context context, boolean isReload, View rootView, String typeToDisplay, MaterialRefreshLayout refresh){
        this.isReload = isReload;
        this.typeToDisplay = typeToDisplay;
        this.rootView = rootView;
        this.refresh = refresh;
        this.context = context;
    }
    
    @Override
    protected FlashablesTypeList doInBackground(Void... params){
        return new FindZips(context,isReload,refresh).run();
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
                context.sendBroadcast(closeDialog);
            return;
        }
        lv.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.list_normal_view,
                        R.id.list_expandable_view)
        );

        if(isLoading)
            context.sendBroadcast(closeDialog);
    }
}