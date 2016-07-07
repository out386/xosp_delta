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

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import delta.out386.xosp.BasketbuildJson.*;
import delta.out386.xosp.JenkinsJson.*;

public class ProcessUpdateJson extends AsyncTask<Void, Void, Void>{
    String json;
    final String TAG = Constants.TAG;
    JenkinsJson updates;
    Context context;
    final String DEVICE = Constants.ROM_ZIP_DEVICE_NAME;

    public ProcessUpdateJson(String json, Context context){
        this.json = json;
        this.context = context;
    }

    @Override
    public Void doInBackground(Void... params){
        Log.v(TAG, "Parsing update JSON");
        if(json.equals(""))
            if(!readOldJson())
                return null;

        Moshi moshi = new Moshi.Builder().build();
        try {
            JsonAdapter<JenkinsJson> jsonAdapter = moshi.adapter(JenkinsJson.class);
            Log.v(TAG, "json : " + json);
            updates = jsonAdapter.fromJson(json);
            if(updates.isMalformed) {
                Log.i(TAG, "Wrong filename format for the ROM zips");
                Intent genericToast = new Intent(Constants.GENERIC_TOAST);
                genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "ROM filename format is wrong. Ask the maintainer to fix it.");
                LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
                return null;
            }
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            if (updates == null || updates.builds.length == 0) {
                Intent genericToast = new Intent(Constants.GENERIC_TOAST);
                genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "ROM descriptors are wrong. Ask the maintainer to fix it.");
                LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
                return null;
            }
            Log.v(TAG, "updates.files.length : " + updates.builds.length);
            updates.process(updates);
            for (builds builds : updates.builds) {
                if(builds.artifacts.length == 0)
                    continue;
                Log.i(TAG, "Build name : " + builds.artifacts[0].fileName);
                Log.i(TAG, "Build MD5 : " + builds.fingerprint[0].hash);
                Log.i(TAG, "Build date : " + builds.artifacts[0].date);
                Log.i(TAG, "Build ID : " + builds.id);
                Log.i(TAG, "Build relative path : " + builds.artifacts[0].relativePath);
            }
        }
        catch(ArrayIndexOutOfBoundsException e){
            Log.e(TAG, e.toString());
        }
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
}
