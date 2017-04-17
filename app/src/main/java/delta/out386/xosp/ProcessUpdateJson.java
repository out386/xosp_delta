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

import com.squareup.moshi.FromJson;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import delta.out386.xosp.BasketbuildJson.*;
import delta.out386.xosp.JenkinsJson.*;

public class ProcessUpdateJson extends AsyncTask<Void, Void, Void>{
    String json;
    String version;
    int newestBuildDateAlt;
    final String TAG = Constants.TAG;
    JenkinsJson updates;
    Context context;

    public ProcessUpdateJson(String json, int newestBuildDateAlt, Context context){
        this.json = json;
        this.context = context;
        this.newestBuildDateAlt = newestBuildDateAlt;
    }

    @Override
    public Void doInBackground(Void... params){
        JsonAdapter<JenkinsJson> jsonAdapter;
        Log.v(TAG, "Parsing update JSON");
        if(json.equals("")) {
            if(!readOldJson()) {
                Intent noJson = new Intent(Constants.JSON_AVAILABILITY);
                noJson.putExtra(Constants.IS_JSON_AVAILABLE, false);
                LocalBroadcastManager.getInstance(context).sendBroadcast(noJson);
                return null;
            } else {
                Intent noJson = new Intent(Constants.JSON_AVAILABILITY);
                noJson.putExtra(Constants.IS_JSON_AVAILABLE, true);
                LocalBroadcastManager.getInstance(context).sendBroadcast(noJson);
            }
        } else {
            Intent noJson = new Intent(Constants.JSON_AVAILABILITY);
            noJson.putExtra(Constants.IS_JSON_AVAILABLE, true);
            LocalBroadcastManager.getInstance(context).sendBroadcast(noJson);
        }

        try {
            Moshi moshi;
            if(Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_JENKINS) {
                moshi = new Moshi.Builder().build();
            }
            else if(Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_BASKETBUILD) {
                moshi = new Moshi.Builder().add(new BasketbuildJsonCopy()).build();
            }
            jsonAdapter = moshi.adapter(JenkinsJson.class);
            updates = jsonAdapter.fromJson(json);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }

        if (updates == null || updates.builds.size() == 0) {
            int installedBuildDate = Tools.romZipDate(Tools.getInstalledRomName(), false).date;

            if (installedBuildDate > -1 && newestBuildDateAlt > installedBuildDate) {
                Intent errorDialog = new Intent(Constants.GENERIC_DIALOG);
                errorDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE,
                        context.getResources().getString(R.string.basketbuild_down_new_available));
                LocalBroadcastManager.getInstance(context).sendBroadcast(errorDialog);
            } else
                Tools.sendGenericToast("ROM descriptors are wrong. Ask the maintainer to fix it.", context);

            return null;
        }
        boolean isUpdateNeeded = Tools.processJenkins(updates, context);
        if(updates.isMalformed) {
            Tools.sendGenericToast("ROM filename format is wrong. Ask the maintainer to fix it.", context);
            return null;
        }
        if(!isUpdateNeeded) {
            Tools.sendGenericToast("No updates are available.", context);
            return null;
        }
        for (builds builds : updates.builds) {
            Log.i(TAG, "Build name : " + builds.artifacts[0].fileName);
            Log.i(TAG, "Build MD5 : " + builds.fingerprint[0].hash);
            Log.i(TAG, "Build date : " + builds.artifacts[0].date);
            Log.i(TAG, "Real build date : " + builds.stringDate);
            Log.i(TAG, "Build ID : " + builds.id);
            Log.i(TAG, "URL : " + builds.artifacts[0].downloadUrl);
            Log.i(TAG, "Build relative path : " + builds.artifacts[0].relativePath);
            if(builds.artifacts[0].size != null)
            Log.i(TAG, "File size : " + builds.artifacts[0].size);
        }
        Intent pendingDownloads = new Intent(Constants.PENDING_DOWNLOADS_INTENT);
        pendingDownloads.putExtra(Constants.PENDING_DOWNLOADS, updates);
        LocalBroadcastManager.getInstance(context).sendBroadcast(pendingDownloads);
        return null;
    }
    public boolean readOldJson() {
        File oldJson = new File(context.getCacheDir().toString() + "/romsList");
        if(!oldJson.exists())
            return false;
        try {
            BufferedReader bw = new BufferedReader(new InputStreamReader(new FileInputStream(oldJson)));
            String tmp;
            while((tmp = bw.readLine()) != null)
                json = json + tmp;
            bw.close();
        }
        catch(Exception e) {
            Log.e(TAG, "readOldJson: ", e);
            return false;
        }
        return true;
    }
    class BasketbuildJsonCopy {
        @FromJson
        JenkinsJson jenkinsFromBasketbuild(BasketbuildJson bbJson) {
            JenkinsJson json = new JenkinsJson();
            json.builds = new ArrayList<>();

            for(BasketbuildJson.file currentBB : bbJson.files) {
                JenkinsJson.builds build = new JenkinsJson.builds();
                build.artifacts = new JenkinsJson.artifacts[1];
                build.fingerprint = new JenkinsJson.fingerprint[1];
                JenkinsJson.artifacts artifact = new JenkinsJson.artifacts();
                JenkinsJson.fingerprint fingerprint = new JenkinsJson.fingerprint();

                fingerprint.hash = currentBB.filemd5;
                build.id = currentBB.filemd5;
                build.timestamp = currentBB.fileTimestamp;
                artifact.fileName = currentBB.file;
                artifact.size = currentBB.filesize;
                artifact.downloadUrl = Constants.DOWNLOAD_FILE_BASKETBUILD1
                        + Constants.ROM_ZIP_DEVICE_NAME
                        + Constants.DOWNLOAD_FILE_BASKETBUILD2
                        + currentBB.file;
                build.artifacts[0] = artifact;
                build.fingerprint[0] = fingerprint;
                json.builds.add(build);
            }
            return json;
        }
    }
}
