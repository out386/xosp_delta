package delta.out386.borkeddelta;

import android.app.Activity;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by J-PC on 4/1/2016.
 */
public class ApplyDelta extends IntentService {
    DeltaData deltaJson;
    String source;
    LoadingDialogFragment loading = new LoadingDialogFragment(R.layout.fragment_delta_apply_dialog);
    String sourceMd5 = null, targetMd5, deltaMd5 = null, diff, targetPath;

    /*public ApplyDelta(DeltaData deltaJson, File source, Context context) {
        this.deltaJson = deltaJson;
        this.targetMd5 = deltaJson.targetMd5;
        this.source = source;
        this.context = context;
        this.diff = source.getParent() + "/diff";
        this.targetPath =  source.getParent() + "/" + deltaJson.target;
    }*/
    public ApplyDelta(){
        super("ApplyDelta");
    }

    /*@Override
    public void onPreExecute() {

        Activity activity = (Activity) context;
        loading.setCancelable(false);
        try {
            loading.show(activity.getFragmentManager(), "dialog");
        } catch (ClassCastException e) {
            Log.e("borked", e.toString());
        }
    }*/

    @Override
    protected void onHandleIntent(Intent intent) {
        String sourceParent = intent.getStringExtra("sourceParent");
        source = intent.getStringExtra("source");
        deltaJson = (DeltaData) intent.getSerializableExtra("deltaJson");
        this.targetMd5 = deltaJson.targetMd5;
        this.diff = sourceParent + "/diff";
        this.targetPath =  sourceParent + "/" + deltaJson.target;
        Log.v("borked", deltaJson.toString());
        try {
            Log.v("borked", "Calculating MD5s");
            Log.v("borked", "Source --> " + source);
            Log.v("borked", "Delta --> " + diff);
            sourceMd5 = Shell.SH.run("md5sum -b " + source).get(0);
            deltaMd5 = Shell.SH.run("md5sum -b " + diff).get(0);
            Log.v("borked", sourceMd5);
            Log.v("borked", deltaMd5);
        } catch (Exception e) {
            Log.e("borked", e.toString());
            return;
        }
        if (!sourceMd5.equalsIgnoreCase(deltaJson.sourceMd5)) {
            Log.e("borked", "sourceMd5 doesn't match");
            return;
        }
        if (!deltaMd5.equalsIgnoreCase(deltaJson.deltaMd5)) {
            Log.e("borked", "deltaMd5 doesn't match");
            return;
        }

        String assetDir = getApplicationContext().getFilesDir().toString();
        String[] assets = {"dedelta", "zipadjust"};
        for (String asset : assets)
            if (!new File(assetDir + "/" + asset).exists()) {
                Log.e("borked", "Assets missing");
                return;
            }
        try {
            String apply = assetDir + "/dedelta " + source + " " + diff + " " + targetPath;
            Log.v("borked", apply);
            String z = Shell.SH.run(apply).get(0);
            Log.v("borked", z);
        } catch (Exception e) {
            Log.e("borked", e.toString());
        }
    }

    /*@Override
    protected void onPostExecute(Void v) {
        loading.dismiss();
        new Md5TargetCheck(targetPath, targetMd5, context).execute();
    }*/
}