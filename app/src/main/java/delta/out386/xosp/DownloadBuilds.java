package delta.out386.xosp;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

import br.com.bemobi.medescope.Medescope;
import br.com.bemobi.medescope.callback.DownloadStatusCallback;
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
    public DownloadBuilds(List<builds> json, MainActivity mainActivity) {
        this.json = json;
        this.mainActivity = mainActivity;
    }
    public void download() {
        Medescope medescope = Medescope.getInstance(mainActivity);
        for(builds current : json) {
            medescope.setApplicationName(mainActivity.getString(R.string.app_name));
            medescope.enqueue(current.id,
                    current.artifacts[0].downloadUrl,
                    current.artifacts[0].fileName,
                    current.stringDate,
                    "{some:'samplejson'}");
            Log.i(Constants.TAG, "Enqueued");
        }

        Medescope.getInstance(mainActivity).subscribeStatus(mainActivity, "DOWNLOAD_ID", new DownloadStatusCallback() {
            @Override
            public void onDownloadNotEnqueued(String downloadId) {
                Log.i(TAG, "not enqueued "+downloadId );
            }

            @Override
            public void onDownloadPaused(String downloadId, int reason) {
                Log.i(TAG, "Pause " + reason);
            }

            @Override
            public void onDownloadInProgress(String downloadId, int progress) {
                Log.i(TAG, "Downloading " + progress);
            }

            @Override
            public void onDownloadOnFinishedWithError(String downloadId, int reason, String data) {
                Log.i(TAG, "Error " + reason +"  "+data);
            }

            @Override
            public void onDownloadOnFinishedWithSuccess(String downloadId, String filePath, String data) {
                Log.i(TAG, "Done ");
            }

            @Override
            public void onDownloadCancelled(String downloadId) {
                Log.i(TAG, "Cancelled");
            }
        });
    }
}
