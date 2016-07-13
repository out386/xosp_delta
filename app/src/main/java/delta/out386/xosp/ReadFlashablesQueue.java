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
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Scanner;

import eu.chainfire.libsuperuser.Shell;

public class ReadFlashablesQueue extends AsyncTask<Void, Void, FlashablesTypeList> {
    Context context;
    View view;
    File delta, source;
    DeltaData deltaJson;
    final String TAG = Constants.TAG;
    FlashablesTypeList flashablesTypeList;
    Intent closeDialog=new Intent(), messageDialog = new Intent(Constants.GENERIC_DIALOG);

    public ReadFlashablesQueue(Context context, View view, FlashablesTypeList flashablesTypeList) {
        this.context = context;
        this.view = view;
        this.flashablesTypeList = flashablesTypeList;
        closeDialog.setAction(Constants.ACTION_CLOSE_DIALOG);
    }
    @Override
    public FlashablesTypeList doInBackground(Void... v) {
        if(flashablesTypeList == null)
            return null;
        if(!flashablesTypeList.deltas.isEmpty()) {
            delta = flashablesTypeList.deltas.get(0).file;
            deltaJson = targetPath(delta);
        }
        return flashablesTypeList;
    }
    @Override
    protected void onPostExecute(final FlashablesTypeList output){
        TextView romName = (TextView)view.findViewById(R.id.queueRomNameText);
        TextView romPath = (TextView)view.findViewById(R.id.queueRomPathText);
        TextView deltaName = (TextView)view.findViewById(R.id.queueDeltaNameText);
        TextView deltaPath = (TextView)view.findViewById(R.id.queueDeltaPathText);
        TextView targetName = (TextView)view.findViewById(R.id.queueTargetNameText);
        TextView targetPathDir = (TextView)view.findViewById(R.id.queueTargetPathText);
        TextView queueEmptyTextview = (TextView)view.findViewById(R.id.queueEmptyTextview);
        RelativeLayout queueEmptyLayout = (RelativeLayout)view.findViewById(R.id.queueEmptyLayout);
        RelativeLayout queueReadyLayout = (RelativeLayout)view.findViewById(R.id.queueReadyLayout);
        Button queueClearButton = (Button)view.findViewById(R.id.queueClearButton);
        Button queueApplyButton = (Button)view.findViewById(R.id.queueApplyButton);

        if(output == null
                || output.roms == null
                || output.deltas == null
                || (output.roms.size() == 0 && output.deltas.size() == 0)) {
            queueEmptyTextview.setText("No base ROM and no deltas found. Please move the zip of whichever version of XOSP you have to the XOSPDelta directory in root of storage.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(output.roms.size() == 0) {
            queueEmptyTextview.setText("No base ROM found. Please move the zip of whichever version of XOSP you have to the XOSPDelta directory in root of storage.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(output.deltas.size() == 0) {
            queueEmptyTextview.setText("No updates available. Pull down to update the list.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(! output.deltas.get(0).file.exists()) {
            queueEmptyTextview.setText("No suitable delta found. Update not required, or you don't have the newest delta. Pull down to update the list.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        queueEmptyLayout.setVisibility(View.GONE);
        queueReadyLayout.setVisibility(View.VISIBLE);

        source = output.roms.get(0).file;


        queueClearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new ReadFlashablesQueue(context, view, null).execute();
            }
        });
        queueApplyButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                try {
                    if(deltaJson != null) {
                        Intent deltaIntent = new Intent(context, ApplyDeltaService.class);
                        deltaIntent.putExtra("source", source.toString());
                        deltaIntent.putExtra("deltaJson", deltaJson);
                        deltaIntent.putExtra("sourceParent", source.getParent());
                        deltaIntent.putExtra("deltaName", delta.toString());
                        context.startService(deltaIntent);
                    }
                }
                catch(Exception e) {
                    Log.e(TAG,e.toString());
                }
            }
        });
        romName.setText(source.getName());
        romPath.setText(source.getParent());
        deltaName.setText(delta.getName());
        deltaPath.setText(delta.getParent());
        if(deltaJson != null) {
            targetName.setText(deltaJson.target);
            targetPathDir.setText(source.getParent());
        }
        queueReadyLayout.setVisibility(RelativeLayout.VISIBLE);
    }
    public DeltaData targetPath(File delta) {
        if(! delta.exists()) {
            LocalBroadcastManager.getInstance(context).sendBroadcast(closeDialog);
            return null;
        }
        File diff = new File(delta.getParent() + "/diff");
        if(diff.exists())
            diff.delete();
        diff = new File(delta.getParent() + "/deltaconfig");
        if(diff.exists())
            diff.delete();
        List<String> resultConfig = Shell.SH.run("unzip " + delta.toString() + " deltaconfig " + "-d " + delta.getParent());
        Log.v(TAG, resultConfig.toString());
        if(resultConfig == null || resultConfig.isEmpty()) {
            Log.e(TAG, "Failed to extract deltaconfig");
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to extract deltaconfig.\nThe delta zip is corrupt. Download it again.");
            context.sendStickyBroadcast(messageDialog);
            return null;
        }
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DeltaData> jsonAdapter = moshi.adapter(DeltaData.class);
        String json = "";
        try {
            Scanner sc = new Scanner(new FileInputStream(delta.getParent() + "/deltaconfig"));
            while(sc.hasNextLine())
                json = json + "\n" + sc.nextLine();
            DeltaData deltaData = jsonAdapter.fromJson(json);
            if(deltaData != null)
                return deltaData;
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }
}