package delta.out386.xosp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
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
    Intent closeDialog=new Intent(), messageDialog = new Intent(Constants.GENERIC_DIALOG);

    public ReadFlashablesQueue(Context context, View view) {
        this.context = context;
        this.view = view;
        closeDialog.setAction(Constants.ACTION_CLOSE_DIALOG);
    }
    @Override
    public FlashablesTypeList doInBackground(Void... v) {

        File f = new File(context.getFilesDir().toString() + "/queue");
        FlashablesTypeList flashablesTypeList = null;
        if(f.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                flashablesTypeList = (FlashablesTypeList) ois.readObject();
                ois.close();
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
            if(flashablesTypeList == null) {
                f.delete();
                return null;
            }
        }
        else
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

        if(output == null || output.roms.isEmpty() && output.deltas.isEmpty()) {
            queueEmptyTextview.setText("No base ROM and no deltas selected. Please select a base ROM and a delta from the ROMs and deltas sections respectively.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(output.roms.isEmpty()) {
            queueEmptyTextview.setText("No base ROM selected. Please select a base ROM from the ROMs section.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(output.deltas.isEmpty()) {
            queueEmptyTextview.setText("No deltas selected. Please select a delta to apply from the deltas section.");
            queueReadyLayout.setVisibility(View.GONE);
            queueEmptyLayout.setVisibility(View.VISIBLE);
            return;
        }
        if(! output.deltas.get(0).file.exists()) {
            queueEmptyTextview.setText("No suitable delta found. Update not required, or you don't have the newest delta. Alternatively, select a ROM and a delta to apply manually.");
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
                File f = new File(context.getFilesDir().toString() + "/queue");
                if (f.exists())
                    f.delete();
                new ReadFlashablesQueue(context, view).execute();
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
            context.sendBroadcast(closeDialog);
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
            context.sendBroadcast(messageDialog);
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