package delta.out386.xosp;
/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/*
 * This file is part of XOSPDelta.
 *
 * XOSPDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * XOSPDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with XOSPDelta. If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
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
    boolean isLoading = false;
    View rootView;
    String typeToDisplay = "roms";
    MaterialRefreshLayout refresh;
    Intent closeDialog = new Intent(Constants.ACTION_CLOSE_DIALOG);
    public SearchZips(Context context, View rootView, String typeToDisplay, MaterialRefreshLayout refresh){
        this.typeToDisplay = typeToDisplay;
        this.rootView = rootView;
        this.refresh = refresh;
        this.context = context;
    }
    
    @Override
    protected FlashablesTypeList doInBackground(Void... params){
        return new FindZips(context,refresh, context.getSharedPreferences("settings", Context.MODE_PRIVATE))
                .run();
    }

    @Override
    protected void onPostExecute(FlashablesTypeList output){

        final MaterialRefreshLayout refresh = (MaterialRefreshLayout)rootView.findViewById(R.id.refresh);
        refresh.setMaterialRefreshListener(new MaterialRefreshListener() {
            @Override
            public void onRefresh(MaterialRefreshLayout materialRefreshLayout) {
                new SearchZips(context, rootView, typeToDisplay, refresh).execute();
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
                LocalBroadcastManager.getInstance(context).sendBroadcast(closeDialog);
            return;
        }
        lv.setAdapter(
                new SlideExpandableListAdapter(
                        adapter,
                        R.id.list_normal_view,
                        R.id.list_expandable_view)
        );

        if(isLoading)
            LocalBroadcastManager.getInstance(context).sendBroadcast(closeDialog);
    }
}