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
import android.view.View;

import com.cjj.MaterialRefreshLayout;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUpdateJson extends AsyncTask<Void, Void, Void> {
    static final int HTTP_READ_TIMEOUT = 30000;
    static final int HTTP_CONNECTION_TIMEOUT = 30000;

    final String TAG = Constants.TAG;
    String url = Constants.UPDATE_JSON_URL + Constants.ROM_ZIP_DEVICE_NAME;
    File f;
    View rootView;
    Context context;
    boolean isSuccessful = false;
    MaterialRefreshLayout emptyRefresh;

    public DownloadUpdateJson(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        f = new File(context.getFilesDir().toString() + "/updateJson");
    }
    @Override
    public void onPreExecute() {
        emptyRefresh = (MaterialRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
    }
    @Override
    public Void doInBackground(Void... v) {
        HttpsURLConnection urlConnection = null;
        long len = 0;
        if (f.exists())
            f.delete();
        try {
            urlConnection = setupHttpsRequest(url);
            if(urlConnection == null)
                return null;
            len = urlConnection.getContentLength();
            InputStream is = urlConnection.getInputStream();
            FileOutputStream os = new FileOutputStream(f, false);
            IOUtils.copy(is, os);
            is.close();
            os.flush();
            os.close();
            isSuccessful = true;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                Log.v(TAG, String.valueOf(len));
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void v){
        emptyRefresh.finishRefresh();
        if(isSuccessful)
            new ProcessUpdateJson(f).execute();
    }
    private HttpsURLConnection setupHttpsRequest(String urlStr){
        URL url;
        HttpsURLConnection urlConnection;

        try {
            url = new URL(urlStr);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(HTTP_READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            if (code != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, "JSON download failed : " + code);
                Log.e(TAG, "With URL : " + url);
                Intent failureMessage = new Intent(Constants.GENERIC_DIALOG_FIRST_START);
                failureMessage.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "Failed to download the list of ROMs and deltas. The error code is " + code);
                LocalBroadcastManager.getInstance(context).sendBroadcast(failureMessage);
                return null;
            }
            return urlConnection;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}