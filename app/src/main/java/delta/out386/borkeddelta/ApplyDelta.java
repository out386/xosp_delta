package delta.out386.borkeddelta;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;

import eu.chainfire.libsuperuser.Shell;
import eu.chainfire.opendelta.Native;

/**
 * Created by J-PC on 4/1/2016.
 */
public class ApplyDelta extends IntentService {
    DeltaData deltaJson;
    String source;
    final String TAG = Constants.TAG;
    LoadingDialogFragment loading = new LoadingDialogFragment(R.layout.fragment_delta_apply_dialog);
    String sourceMd5 = null, targetMd5, deltaMd5 = null, diff, targetPath;
    public ApplyDelta(){
        super("ApplyDelta");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int zipadjust, delta;

        Intent closeDialog = new Intent(), applyDialog = new Intent(Constants.ACTION_APPLY_DIALOG);
        closeDialog.setAction(Constants.ACTION_CLOSE_DIALOG);
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Decompressing input zip");

        String sourceParent = intent.getStringExtra("sourceParent");
        source = intent.getStringExtra("source");
        deltaJson = (DeltaData) intent.getSerializableExtra("deltaJson");
        this.diff = sourceParent + "/diff";
        this.targetPath =  sourceParent + "/" + deltaJson.target;
        Log.v(TAG, deltaJson.toString());

        //Get the fake dialog up
        startActivity(new Intent(this, DeltaDialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));


        try {
            Log.v(TAG, "Calculating MD5s");
            Log.v(TAG, "Source --> " + source);
            Log.v(TAG, "Delta --> " + diff);
            sourceMd5 = Shell.SH.run("md5sum -b " + source).get(0);
            deltaMd5 = Shell.SH.run("md5sum -b " + diff).get(0);
            Log.v(TAG, sourceMd5);
            Log.v(TAG, deltaMd5);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            sendBroadcast(closeDialog);
            return;
        }
        if (!sourceMd5.equalsIgnoreCase(deltaJson.sourceMd5)) {
            Log.e(TAG, "sourceMd5 doesn't match");
            sendBroadcast(closeDialog);
            return;
        }
        if (!deltaMd5.equalsIgnoreCase(deltaJson.deltaMd5)) {
            Log.e(TAG, "deltaMd5 doesn't match");
            sendBroadcast(closeDialog);
            return;
        }

        sendBroadcast(applyDialog);
        
        try {
            String sourceDec = source + ".dec";
            zipadjust = Native.zipadjust(source, sourceDec, 1);
            Log.v(TAG, "Result of decompression : " + zipadjust);

            applyDialog.removeExtra(Constants.DIALOG_MESSAGE);
            applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Applying the delta.\n\nThis could take 20-30 minutes.");
            sendBroadcast(applyDialog);

            Log.v(TAG, "Applying delta");
            delta = Native.dedelta(sourceDec, diff, targetPath);
            Log.v(TAG, "Result of delta apply : " + delta);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            sendBroadcast(closeDialog);
        }

        Log.v(TAG, "Verifying MD5 of built");
        applyDialog.removeExtra(Constants.DIALOG_MESSAGE);
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Verifying MD5 of built delta");
        sendBroadcast(applyDialog);

        try{
            targetMd5 = Shell.SH.run("md5sum -b " + targetPath).get(0);
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        finally {
            sendBroadcast(closeDialog);
        }
        if(!deltaJson.targetMd5.equalsIgnoreCase(targetMd5))
            Log.e(TAG, "Target malformed");
        else
            startActivity(new Intent(this, DialogActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}