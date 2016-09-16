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
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WriteFlashablesQueue extends AsyncTask<Void, Void, Void> {
    Flashables flashables;
    Context context;
    final String TAG = Constants.TAG;
    boolean isReload = false;

    public WriteFlashablesQueue(Flashables flashables, Context context, boolean isReload) {
        this.flashables = flashables;
        this.context = context;
        this.isReload = isReload;
    }
    @Override
    public Void doInBackground(Void... v) {
        File f = new File(context.getFilesDir().toString() + "/queue");
        FlashablesTypeList flashablesTypeList = null;
        if(isReload && f.exists())
            f.delete();
        if(f.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                flashablesTypeList = (FlashablesTypeList) ois.readObject();
                ois.close();
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        else
            flashablesTypeList = new FlashablesTypeList();
        if(flashables != null && flashablesTypeList != null)
            flashablesTypeList.addFlashable(flashables);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(flashablesTypeList);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }
}
