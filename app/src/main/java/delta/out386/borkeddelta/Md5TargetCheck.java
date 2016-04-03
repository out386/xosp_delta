package delta.out386.borkeddelta;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by J-PC on 4/2/2016.
 */
public class Md5TargetCheck extends AsyncTask<Void, Void, Void> {
    String targetPath, targetMd5Json;
    LoadingDialogFragment loading = new LoadingDialogFragment(R.layout.fragment_md5_dialog);
    Context context;
    public Md5TargetCheck(String targetPath, String targetMd5Json, Context context) {
        this.targetPath = targetPath;
        this.targetMd5Json = targetMd5Json;
        this.context = context;
    }
    @Override
    public void onPreExecute() {
        Activity activity = (Activity) context;
        loading.setCancelable(false);
        try {
            loading.show(activity.getFragmentManager(), "dialog");
        } catch (ClassCastException e) {
            Log.e("borked", e.toString());
        }
    }

    @Override
    public Void doInBackground(Void... params) {
        String targetMd5 = null;
        try{
            targetMd5 = Shell.SH.run("md5sum -b " + targetPath).get(0);
        }
        catch(Exception e) {
            Log.e("borked", e.toString());
        }
        if(!targetMd5Json.equalsIgnoreCase(targetMd5))
            Log.e("borked", "Target malformed");
        return null;
    }
    @Override
    protected void onPostExecute(Void v) {

        loading.dismiss();
    }
}
