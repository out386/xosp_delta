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

public class ProcessUpdateJson extends AsyncTask<Void, Void, Void>{
    String json;
    final String TAG = Constants.TAG;
    BasketbuildJson updates;
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
            JsonAdapter<BasketbuildJson> jsonAdapter = moshi.adapter(BasketbuildJson.class);
            Log.v(TAG, "json : " + json);
            updates = jsonAdapter.fromJson(json);
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            if (updates == null || updates.files.length == 0) {
                Intent genericToast = new Intent(Constants.GENERIC_TOAST);
                genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, "ROM descriptors are wrong. Ask the maintainer to fix it.");
                LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
                return null;
            }
            Log.v(TAG, "updates.files.length : " + updates.files.length);
            updates.process(updates);
            for (file file : updates.files) {
                Log.i(TAG, "File : " + file.file);
                Log.i(TAG, "File size: " + file.filesize);
                Log.i(TAG, "File MD5: " + file.filemd5);
                Log.i(TAG, "File date: " + file.date);
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
