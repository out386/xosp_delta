package delta.out386.borkeddelta;

import android.app.IntentService;
import android.content.Intent;
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
    public ApplyDelta(){
        super("ApplyDelta");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Intent closeDialog = new Intent(), applyDialog = new Intent(Constants.ACTION_APPLY_DIALOG), md5Dialog = new Intent(Constants.ACTION_APPLY_DIALOG);
        closeDialog.setAction(Constants.ACTION_CLOSE_DIALOG);
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Applying delta. This could take \n\nanywhere between 10-30 minutes.");
        md5Dialog.putExtra(Constants.DIALOG_MESSAGE, "Verifying MD5 of built zip.");

        String sourceParent = intent.getStringExtra("sourceParent");
        source = intent.getStringExtra("source");
        deltaJson = (DeltaData) intent.getSerializableExtra("deltaJson");
        this.diff = sourceParent + "/diff";
        this.targetPath =  sourceParent + "/" + deltaJson.target;
        Log.v("borked", deltaJson.toString());

        //Get the fake dialog up
        startActivity(new Intent(this, DeltaDialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


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
            sendBroadcast(closeDialog);
            return;
        }
        if (!sourceMd5.equalsIgnoreCase(deltaJson.sourceMd5)) {
            Log.e("borked", "sourceMd5 doesn't match");
            sendBroadcast(closeDialog);
            return;
        }
        if (!deltaMd5.equalsIgnoreCase(deltaJson.deltaMd5)) {
            Log.e("borked", "deltaMd5 doesn't match");
            sendBroadcast(closeDialog);
            return;
        }

        sendBroadcast(applyDialog);

        String assetDir = getApplicationContext().getFilesDir().toString();
        String[] assets = {"dedelta", "zipadjust"};
        for (String asset : assets)
            if (!new File(assetDir + "/" + asset).exists()) {
                Log.e("borked", "Assets missing");
                sendBroadcast(closeDialog);
                return;
            }
        try {
            String apply = assetDir + "/dedelta " + source + " " + diff + " " + targetPath;
            Log.v("borked", apply);
            String z = Shell.SH.run(apply).get(0);
            // run will return size > 0 only if there is a problem.
            Log.v("borked", z);
        } catch (Exception e) {
            Log.e("borked", e.toString());
        }

        Log.v("borked", "Verifying MD5 of built");
        //Verify MD5 of built delta
        sendBroadcast(md5Dialog);

        try{
            targetMd5 = Shell.SH.run("md5sum -b " + targetPath).get(0);
        }
        catch(Exception e) {
            Log.e("borked", e.toString());
        }
        finally {
            sendBroadcast(closeDialog);
        }
        if(!deltaJson.targetMd5.equalsIgnoreCase(targetMd5))
            Log.e("borked", "Target malformed");
        else
            startActivity(new Intent(this, DialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}