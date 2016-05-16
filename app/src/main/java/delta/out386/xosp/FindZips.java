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
import android.content.SharedPreferences;
import android.os.Environment;
import android.util.Log;

import com.cjj.MaterialRefreshLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class FindZips {
    Context context;
    boolean isReload=false, isLoading = false;
    final String TAG = Constants.TAG;
    File f = null;
    MaterialRefreshLayout refresh;
    SharedPreferences preferences;

    public FindZips(Context context, boolean isReload, MaterialRefreshLayout refresh, SharedPreferences preferences){
        this.isReload = isReload;
        this.refresh = refresh;
        this.context = context;
        this.preferences = preferences;
    }

    public FlashablesTypeList run() {
        FlashablesTypeList output = null;
        File directory = null;
        boolean directoryExists = true;
        String location = preferences.getString("location", Environment.getExternalStorageDirectory().getAbsolutePath());
        Log.v(TAG, location);
        File storage = new File(location);
        f = new File(context.getFilesDir().toString() + "/FlashablesTypeList");
        if (!isReload && !f.exists()) {
            isLoading = true;
            // Get the fake dialog up
            context.startActivity(new Intent(context, DeltaDialogActivity.class)
                    .setAction(Constants.ACTION_APPLY_DIALOG)
                    .putExtra(Constants.DIALOG_MESSAGE, "Loading list of files")
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        try {
            if(!storage.exists()) {
                Log.e(TAG, "Storage location does not exist");
                return null;
            }
                if (Environment.getExternalStorageState(storage).equals(Environment.MEDIA_MOUNTED))
                    directory = new File(location + "/XOSPDelta");
            }
            catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
            if (directory == null)
                return null;
            if (!directory.exists())
                directoryExists = directory.mkdir();
        if (!directoryExists) {
            Log.e(TAG, "Couldn't create storage directory");
            return null;
        }
        if (!f.exists() || isReload) {
            Collection<File> zipsCollection;
            if (f.exists())
                f.delete();
            zipsCollection = FileUtils.listFiles(directory, new String[]{"zip"}, false);
            output = new FilesCategorize().run(zipsCollection);
            ObjectOutputStream oos;
            if (output != null) {
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(f));
                    oos.writeObject(output);
                    oos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                refreshDone();
                return output;
            }
        } else {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                output = (FlashablesTypeList) ois.readObject();
                ois.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                refreshDone();
            }
        }
        refreshDone();
        return output;
    }

    public void refreshDone()
    {
        if(refresh == null)
            return;
        try {
            // To display the animation even if the reload happens fast, removes stutters
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
        refresh.finishRefresh();
    }
}
