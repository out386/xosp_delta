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
    String sourceMd5 = null, targetMd5, deltaMd5 = null, diff, targetPath;
    public ApplyDelta(){
        super("ApplyDelta");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int zipadjust, delta;

        Intent closeDialog = new Intent(),
                applyDialog = new Intent(Constants.ACTION_APPLY_DIALOG),
                messageDialog = new Intent(Constants.GENERIC_DIALOG);
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
            String error = e.toString();
            Log.e(TAG, error);
            sendBroadcast(closeDialog);
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Couldn't calculate MD5.\nMake sure that you have busybox installed.\n"
                    + error);
            sendBroadcast(messageDialog);
            return;
        }
        if (!sourceMd5.equalsIgnoreCase(deltaJson.sourceMd5)) {
            Log.e(TAG, "sourceMd5 doesn't match");
            sendBroadcast(closeDialog);
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Source MD5 mismatch.\nYour base rom is corrupted or not compatable with this delta.\nCheck if you've selected the correct file or try re-downloading.\nAlso check for free space.");
            sendBroadcast(messageDialog);
            return;
        }
        if (!deltaMd5.equalsIgnoreCase(deltaJson.deltaMd5)) {
            Log.e(TAG, "deltaMd5 doesn't match");
            sendBroadcast(closeDialog);
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Delta MD5 mismatch.\nYour delta file is corrupt or you are out of space.");
            sendBroadcast(messageDialog);
            return;
        }

        sendBroadcast(applyDialog);
        String sourceDec = source + ".dec";
        try {
            zipadjust = Native.zipadjust(source, sourceDec, 1);
            Log.v(TAG, "Result of decompression : " + zipadjust);
        }
        catch(Exception e) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Decompression of base ROM failed.\nMake sure that you have enough free space.");
            sendBroadcast(closeDialog);
            sendBroadcast(messageDialog);
            Log.e(TAG, e.toString());
            return;
        }
        if(zipadjust != 1)
        {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Decompression of base ROM failed.\nMake sure that you have enough free space.");
            sendBroadcast(closeDialog);
            sendBroadcast(messageDialog);
            return;
        }
        try {
            applyDialog.removeExtra(Constants.DIALOG_MESSAGE);
            applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Applying the delta.\n\nThis could take 20-30 minutes.");
            sendBroadcast(applyDialog);

            Log.v(TAG, "Applying delta");
            delta = Native.dedelta(sourceDec, diff, targetPath);
            Log.v(TAG, "Result of delta apply : " + delta);
        } catch (Exception e) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to apply the delta.\nMake sure that you are not out of space.");
            sendBroadcast(closeDialog);
            sendBroadcast(messageDialog);
            Log.e(TAG, e.toString());
            return;
        }
        if(delta != 1) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to apply the delta.\nMake sure that you are not out of space.");
            sendBroadcast(closeDialog);
            sendBroadcast(messageDialog);
            return;
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
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to verify MD5s.\nMake sure that you have busybox installed.");
            sendBroadcast(messageDialog);
            sendBroadcast(closeDialog);
            return;
        }
            sendBroadcast(closeDialog);
        if(!deltaJson.targetMd5.equalsIgnoreCase(targetMd5)) {
            Log.e(TAG, "Target malformed");
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Applied the delta, but the generated zip is malformed.\nMake sure that you are not out of space.");
            sendBroadcast(messageDialog);
        }
        else {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Delta applied successfully");
            sendBroadcast(messageDialog);
        }
    }
}