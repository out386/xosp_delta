package delta.out386.borkeddelta;

import android.content.Context;
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
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import java.util.Scanner;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by J-PC on 3/30/2016.
 */
public class ReadFlashablesQueue extends AsyncTask<Void, Void, FlashablesTypeList> {
    Context context;
    View view;
    File source, target, delta;
    public ReadFlashablesQueue(Context context, View view) {
        this.context = context;
        this.view = view;
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
            catch(FileNotFoundException e) {
                Log.e("borked", e.toString());
            }
            catch(IOException e) {
                Log.e("borked", e.toString());
            }
            catch(ClassNotFoundException e) {
                Log.e("borked", e.toString());
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
            target = targetPath(delta);
        }
        return flashablesTypeList;
    }
    @Override
    protected void onPostExecute(final FlashablesTypeList output){
        TextView romName = (TextView)view.findViewById(R.id.queueRomNameText);
        TextView romPath = (TextView)view.findViewById(R.id.queueRomPathText);
        TextView deltaName = (TextView)view.findViewById(R.id.queueDeltaNameText);
        TextView deltaPath = (TextView)view.findViewById(R.id.queueDeltaPathText);
        TextView queueEmptyTextview = (TextView)view.findViewById(R.id.queueEmptyTextview);
        RelativeLayout queueEmptyLayout = (RelativeLayout)view.findViewById(R.id.queueEmptyLayout);
        RelativeLayout queueReadyLayout = (RelativeLayout)view.findViewById(R.id.queueReadyLayout);
        Button queueClearButton = (Button)view.findViewById(R.id.queueClearButton);
        Button queueApplyButton = (Button)view.findViewById(R.id.queueApplyButton);

        if(output == null || output.roms.isEmpty() && output.deltas.isEmpty()) {
            queueEmptyTextview.setText("No target ROM and no deltas selected. Please select a target ROM and a delta from the ROMs and deltas sections respectively.");
            queueReadyLayout.setVisibility(RelativeLayout.GONE);
            queueEmptyLayout.setVisibility(RelativeLayout.VISIBLE);
            return;
        }
        if(output.roms.isEmpty()) {
            queueEmptyTextview.setText("No target ROM selected. Please select a target ROM from the ROMs section.");
            queueReadyLayout.setVisibility(RelativeLayout.GONE);
            queueEmptyLayout.setVisibility(RelativeLayout.VISIBLE);
            return;
        }
        if(output.deltas.isEmpty()) {
            queueEmptyTextview.setText("No deltas selected. Please select a delta to apply from the deltas section.");
            queueReadyLayout.setVisibility(RelativeLayout.GONE);
            queueEmptyLayout.setVisibility(RelativeLayout.VISIBLE);
            return;
        }
        queueEmptyLayout.setVisibility(RelativeLayout.GONE);

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
                DeltaData data = null;
                try {
                    data = new DeltaData(source.toString(), target.toString(), delta.toString());
                }
                catch(Exception e) {
                    Log.e("borked",e.toString());
                }
                new WriteJson(data, context).execute();
            }
        });
        romName.setText(source.getName());
        romPath.setText(source.getParent());
        deltaName.setText(delta.getName());
        deltaPath.setText(delta.getParent());
        queueReadyLayout.setVisibility(RelativeLayout.VISIBLE);
    }
    public File targetPath(File delta) {
        List<String> result = Shell.SU.run("unzip " + delta.toString() + "deltaconfig " + "-d /sdcard/borkeddelta");
        if(result == null || result.isEmpty()) {
            Log.e("borked", "Failed to extract deltaconfig");
            return null;
        }
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DeltaData> jsonAdapter = moshi.adapter(DeltaData.class);
        String json = "";
        try {
            Scanner sc = new Scanner(new FileInputStream(delta.getParent() + "/deltaconfig"));
            while(sc.hasNextLine())
                json = json + "\n" + sc.nextLine();
            Log.v("borked", json);
            DeltaData deltaData = jsonAdapter.fromJson(json);
            if(deltaData != null) {
                Log.v("borked", deltaData.target);
                return new File(deltaData.target);
            }
        }
        catch(Exception e) {
            Log.e("borked", e.toString());
        }
        return null;
    }
}