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
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.View;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUpdateJson extends AsyncTask<Void, Void, Void> {
    static final int HTTP_READ_TIMEOUT = 30000;
    static final int HTTP_CONNECTION_TIMEOUT = 30000;

    final String TAG = Constants.TAG;
    String url = Constants.UPDATE_JSON_URL + Constants.ROM_ZIP_DEVICE_NAME, json = "", jsonLine;
    File jsonStore;
    View rootView;
    Context context;
    SwipeRefreshLayout emptyRefresh;

    public DownloadUpdateJson(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        jsonStore = new File(context.getCacheDir().toString() + "/romsList");
    }
    @Override
    public void onPreExecute() {
        emptyRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
    }
    @Override
    public Void doInBackground(Void... v) {
        HttpsURLConnection urlConnection = null;
        try {
            urlConnection = setupHttpsRequest(url);
            if(urlConnection == null)
                return null;
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            while((jsonLine = br.readLine()) != null)
                json = json + jsonLine;
            br.close();

            if(jsonStore.exists())
                jsonStore.delete();
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonStore)));
            bw.write(json);
            bw.close();
            Log.i(TAG, jsonStore.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void v){
        emptyRefresh.post(new Runnable() {
            @Override
            public void run() {
                emptyRefresh.setRefreshing(false);
            }
        });
            new ProcessUpdateJson(json, context).execute();
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
                Intent genericToast = new Intent(Constants.GENERIC_TOAST);
                genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "Failed to download the list of ROMs and deltas. The error code is " + code);
                LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
                return null;
            }
            return urlConnection;
        }
        catch(UnknownHostException e) {
            Intent genericToast = new Intent(Constants.GENERIC_TOAST);
            genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "Could not connect to the download server");
            LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
        }
        catch (Exception e) {
            Log.e(TAG, "URLConnection : " + e.toString());
        }
        return null;
    }
}