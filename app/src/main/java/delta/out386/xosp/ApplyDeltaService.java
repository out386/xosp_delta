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

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;
import java.util.List;

import eu.chainfire.libsuperuser.Shell;
import eu.chainfire.opendelta.Native;

public class ApplyDeltaService extends IntentService {
    DeltaData deltaJson;
    String source;
    final String TAG = Constants.TAG;
    String sourceMd5 = null, targetMd5, deltaMd5 = null, diff, targetPath;
    public ApplyDeltaService(){
        super("ApplyDeltaService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int zipadjust, delta;
        String deltaName;
        List<String> deltaExtractResult = null;

        Intent applyDialog = new Intent(Constants.ACTION_APPLY_DIALOG), messageDialog = new Intent(Constants.GENERIC_DIALOG);

        String sourceParent = intent.getStringExtra("sourceParent"), diffExtractCommand = "";
        source = intent.getStringExtra("source");
        deltaJson = (DeltaData) intent.getSerializableExtra("deltaJson");
        deltaName= intent.getStringExtra("deltaName");
        this.diff = sourceParent + "/diff";
        this.targetPath =  sourceParent + "/" + deltaJson.target;

        // Get the fake dialog up
        startActivity(new Intent(this, DeltaDialogActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

        /**
         *  Delay needed as the dialog activity needs time to register
         * the broadcast receiver
         */
        try {
            Thread.sleep(90);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Extracting the delta.");
        sendBroadcast(applyDialog);

        try {
            diffExtractCommand = "unzip " + deltaName + " diff " + "-d " + sourceParent;
            deltaExtractResult = Shell.SH.run(diffExtractCommand);
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Could not extract the delta.\nThe delta zip is corrupt. Download it again.");
            sendBroadcast(messageDialog);
            return;
        }
        if(deltaExtractResult == null || deltaExtractResult.size() == 0) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Could not extract the delta.\nThe delta zip is corrupt. Download it again.");
            sendBroadcast(messageDialog);
            return;
        }

        applyDialog.removeExtra(Constants.DIALOG_MESSAGE);
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Verifying MD5s.");
        sendBroadcast(applyDialog);

        Log.v(TAG, diffExtractCommand);
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
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Couldn't calculate MD5.\nMake sure that you have busybox installed."
                    + error);
            sendBroadcast(messageDialog);
            return;
        }
        if (!(sourceMd5.equalsIgnoreCase(deltaJson.sourceMd5) || sourceMd5.equalsIgnoreCase(deltaJson.sourceDecMd5))) {
            Log.e(TAG, "sourceMd5 doesn't match");
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Source MD5 mismatch.\nYour base rom is corrupted or not compatable with this delta. Check if you've selected the correct file or try re-downloading. Also check for free space.");
            sendBroadcast(messageDialog);
            return;
        }
        if (!deltaMd5.equalsIgnoreCase(deltaJson.deltaMd5)) {
            Log.e(TAG, "deltaMd5 doesn't match");
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Delta MD5 mismatch.\nYour delta file is corrupt or you are out of space.");
            sendBroadcast(messageDialog);
            return;
        }

        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Decompressing input zip");
        sendBroadcast(applyDialog);
        String sourceDec = source + ".dec";
        try {
            zipadjust = Native.zipadjust(source, sourceDec, 1);
            Log.v(TAG, "Result of decompression : " + zipadjust);
        }
        catch(Exception e) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Decompression of base ROM failed.\nMake sure that you have enough free space.");
            sendBroadcast(messageDialog);
            Log.e(TAG, e.toString());
            return;
        }
        if(zipadjust != 1)
        {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Decompression of base ROM failed.\nMake sure that you have enough free space.");
            sendBroadcast(messageDialog);
            return;
        }

        applyDialog.removeExtra(Constants.DIALOG_MESSAGE);
        applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Applying the delta.");
        sendBroadcast(applyDialog);
        File targetFile = new File(targetPath);
        if(targetFile.exists())
            targetFile.delete();
        Log.v(TAG, "Applying delta");
        Intent sizeIntent = new Intent(this, BuiltSizeService.class);
        sizeIntent.putExtra("target", targetPath);
        sizeIntent.putExtra("targetSize", deltaJson.targetSize);
        startService(sizeIntent);
        try {
            delta = Native.dedelta(sourceDec, diff, targetPath);
            Log.v(TAG, "Result of delta apply : " + delta);
        } catch (Exception e) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to apply the delta.\nMake sure that you are not out of space.");
            sendBroadcast(messageDialog);
            Log.e(TAG, e.toString());
            return;
        }
        if(delta != 1) {
            messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to apply the delta.\nMake sure that you are not out of space.");
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
            return;
        }
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