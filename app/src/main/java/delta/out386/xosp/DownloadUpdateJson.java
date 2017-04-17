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
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

class DownloadUpdateJson extends AsyncTask<Void, Void, Void> {
    private static final int HTTP_READ_TIMEOUT = 30000;
    private static final int HTTP_CONNECTION_TIMEOUT = 30000;

    final String TAG = Constants.TAG;
    private String url;
    private String json = "";
    private int newestDateAlt;
    private File jsonStore;
    private View rootView;
    Context context;
    private SwipeRefreshLayout emptyRefresh;

    DownloadUpdateJson(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
        jsonStore = new File(context.getCacheDir().toString() + "/romsList");
        if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_BASKETBUILD))
            url = Constants.UPDATE_JSON_URL_BASKETBUILD1 + Constants.ROM_ZIP_DEVICE_NAME + Constants.UPDATE_JSON_URL_BASKETBUILD2;
        else if(Constants.CURRENT_DOWNLOADS_API_TYPE.equals(Constants.DOWNLOADS_API_TYPE_JENKINS))
            url = Constants.UPDATE_JSON_URL_JENKINS_1 + Constants.ROM_ZIP_DEVICE_NAME + Constants.UPDATE_JSON_URL_JENKINS_2;
    }
    @Override
    public void onPreExecute() {
        emptyRefresh = (SwipeRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
    }
    @Override
    public Void doInBackground(Void... v) {
        Log.i(TAG, url);
        try {
            json = readHTTPS(url);
            newestDateAlt = Tools.romZipDate(
                    readHTTPS(Constants.NEWEST_BUILD_URL_ALT), false)
            .date;

            if(jsonStore.exists())
                jsonStore.delete();

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(jsonStore)));
            bw.write(json);
            bw.close();
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        return null;
    }

    @Override
    public void onPostExecute(Void v){
        if(emptyRefresh != null) {
            emptyRefresh.post(new Runnable() {
                @Override
                public void run() {
                    emptyRefresh.setRefreshing(false);
                }
            });
        }
            new ProcessUpdateJson(json, newestDateAlt, context).execute();
    }
    private HttpsURLConnection setupHttpsRequest(String urlStr){
        URL url;
        HttpsURLConnection urlConnection;

        if(!Tools.checkHost(Constants.CONNECTIVITY_CHECK_URL)) {
            Intent genericToast = new Intent(Constants.GENERIC_TOAST);
            genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "No internet connection is available.");
            LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
            return null;
        }
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
                Log.i(TAG, "JSON download failed");
                LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
                return null;
            }
            return urlConnection;
        }
        catch(UnknownHostException | ConnectException e) {
            Intent genericToast = new Intent(Constants.GENERIC_TOAST);
            genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "Could not connect to the download server");
            LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
        }
        catch (Exception e) {
            Log.e(TAG, "URLConnection : " + e.toString());
        }
        return null;
    }

    String readHTTPS (String url) {
        HttpsURLConnection urlConnection = setupHttpsRequest(url);
        String content = "";
        if(urlConnection == null)
            return null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String jsonLine;
            while ((jsonLine = br.readLine()) != null)
                content = content + jsonLine;
            br.close();
        } catch (IOException e) {}
        finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
        return content;
    }
}