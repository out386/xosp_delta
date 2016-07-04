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
import android.widget.Toast;

import com.cjj.MaterialRefreshLayout;

import org.apache.commons.io.IOUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.UnknownHostException;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUpdateJson extends AsyncTask<Void, Void, Void> {
    static final int HTTP_READ_TIMEOUT = 30000;
    static final int HTTP_CONNECTION_TIMEOUT = 30000;

    final String TAG = Constants.TAG;
    String url = Constants.UPDATE_JSON_URL + Constants.ROM_ZIP_DEVICE_NAME;
    String json = "", jsonLine;
    View rootView;
    Context context;
    boolean isSuccessful = false;
    MaterialRefreshLayout emptyRefresh;

    public DownloadUpdateJson(Context context, View rootView) {
        this.context = context;
        this.rootView = rootView;
    }
    @Override
    public void onPreExecute() {
        emptyRefresh = (MaterialRefreshLayout) rootView.findViewById(R.id.emptyRefresh);
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
            isSuccessful = true;
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
        emptyRefresh.finishRefresh();
        if(isSuccessful)
            new ProcessUpdateJson(json).execute();
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
                Toast.makeText(context, "Failed to download the list of ROMs and deltas. The error code is " + code, Toast.LENGTH_SHORT).show();
                return null;
            }
            return urlConnection;
        }
        catch(UnknownHostException e) {
            Toast.makeText(context, "Could not connect to the download server", Toast.LENGTH_SHORT).show();
        }
        catch (Exception e) {
            Log.e(TAG, "URLConnection : " + e.toString());
        }
        return null;
    }
}