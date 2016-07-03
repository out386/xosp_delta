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
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class ProcessUpdateJson extends AsyncTask<Void, Void, Void>{
    File file;
    final String TAG = Constants.TAG;
    BasketbuildJson updates;
    final String DEVICE = Constants.ROM_ZIP_DEVICE_NAME;

    public ProcessUpdateJson(File file){
        this.file = file;
    }

    @Override
    public Void doInBackground(Void... params){

        if(!file.exists())
            return null;
        Log.v(TAG, "Parsing update JSON");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<BasketbuildJson> jsonAdapter = moshi.adapter(BasketbuildJson.class);
        String json = "";
        try {
            Scanner sc = new Scanner(new FileInputStream(file));
            while(sc.hasNextLine())
                json = json + "\n" + sc.nextLine();
            Log.v(TAG, "json : " + json);
            updates = jsonAdapter.fromJson(json);
            Log.v(TAG, "updates.length : " + updates.files.length);
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }

        try {
            String temp = "";
            if (updates == null) {
                Log.e(TAG, "ROM descriptors are wrong. Ask the maintainer to fix it.");
                return null;
            }
            for (folder folder : updates.folders) {
                Log.i(TAG, "Folder : " + folder.folder);
            }
            for (file file : updates.files) {
                file.process();
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
}
