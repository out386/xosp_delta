package delta.out386.xosp;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.util.List;

import br.com.bemobi.medescope.Medescope;
import br.com.bemobi.medescope.callback.DownloadStatusCallback;
import br.com.bemobi.medescope.exception.DirectoryNotMountedException;
import br.com.bemobi.medescope.exception.PathNotFoundException;
import br.com.bemobi.medescope.model.DownloadRequest;
import delta.out386.xosp.JenkinsJson.builds;

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
public class DownloadBuilds {
    List<builds> json;
    MainActivity mainActivity;
    String TAG = Constants.TAG;
    Medescope medescope;
    String targetDir;
    public DownloadBuilds(List<builds> json, MainActivity mainActivity) {
        this.json = json;
        this.mainActivity = mainActivity;
        targetDir = mainActivity
                .getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getString("location", Environment.getExternalStorageDirectory()
                        .getAbsolutePath())
                + "/XOSPDelta";
        medescope = Medescope.getInstance(mainActivity);
    }

    public void download(final int i) {
        if(i >= json.size())
            return;

        final builds current = json.get(i);
        medescope.setApplicationName(mainActivity.getString(R.string.app_name));
        if(! current.isDownloaded) {
            medescope.enqueue(current.id,
                    current.artifacts[0].downloadUrl,
                    current.artifacts[0].fileName,
                    "XOSP - " + current.stringDate + " build",
                    "{some:'samplejson'}");
            Log.i(Constants.TAG, "Enqueued " + current.id);
        }

        medescope.subscribeStatus(mainActivity, current.id, new DownloadStatusCallback() {
            @Override
            public void onDownloadNotEnqueued(String downloadId) {
                Log.i(TAG, "not enqueued " + downloadId);
                try {
                    medescope.unsubscribeStatus(mainActivity);
                } catch(IllegalArgumentException e) {}
                current.downloadProgress = -2;
                progress(-1, downloadId);
                download(i + 1);
            }

            @Override
            public void onDownloadPaused(String downloadId, int reason) {
                progress(Integer.MAX_VALUE, downloadId);
            }

            @Override
            public void onDownloadInProgress(String downloadId, int progress) {
                progress(progress, downloadId);
            }

            @Override
            public void onDownloadOnFinishedWithError(String downloadId, int reason, String data) {
                Log.i(TAG, "Error " + downloadId + "  " + reason + "  " + data);
                try {
                    medescope.unsubscribeStatus(mainActivity);
                } catch(IllegalArgumentException e) {}
                progress(-1, downloadId);
                current.downloadProgress = -2;
                download(i + 1);
            }

            @Override
            public void onDownloadOnFinishedWithSuccess(String downloadId, String filePath, String data) {
                Log.i(TAG, "Done " + downloadId);
                try {
                    medescope.unsubscribeStatus(mainActivity);
                } catch(IllegalArgumentException e) {}
                current.isDownloaded = true;
                moveFile(current.artifacts[0].fileName);
                download(i + 1);
            }

            @Override
            public void onDownloadCancelled(String downloadId) {
                current.downloadProgress = -2;
                Log.i(TAG, "Cancelled" + downloadId);
            }
        });
    }
    public void progress(int i, String id) {
        Intent progress = new Intent(Constants.DOWNLOADS_PROGRESS);
        progress.putExtra(Constants.DOWNLOADS_PROGRESS_VALUE, i);
        progress.putExtra(Constants.DOWNLOADS_PROGRESS_ID, id);
        LocalBroadcastManager.getInstance(mainActivity).sendBroadcast(progress);
    }
    public void moveFile(String fileName) {
        File oldFile = null;
        try {
            oldFile = new File(medescope.getDownloadDirectoryToRead(fileName));
        } catch (DirectoryNotMountedException | PathNotFoundException e) {
            e.printStackTrace();
        }
        if(oldFile != null)
            oldFile.renameTo(new File(targetDir + "/" + fileName));
    }
}
