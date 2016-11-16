/*
 * Copyright (C) 2015-2016 Willi Ye <williye97@gmail.com>
 *
 * This file was originally a part of Kernel Adiutor.
 *
 * Added in XOSPDelta by Ritayan Chakraborty (out386)
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
 * along with XOSPDelta.  If not, see <http://www.gnu.org/licenses/>.
 *
 */
package com.grarak.kerneladiutor.utils;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.StringRes;
import android.util.Log;
import android.widget.Toast;

import com.grarak.kerneladiutor.utils.root.RootFile;
import eu.chainfire.libsuperuser.Shell;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by willi on 14.04.16.
 */
public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static String getExternalStorage() {
        String path = Shell.SU.run("echo ${SECONDARY_STORAGE%%:*}").get(0);
        return path.contains("/") ? path : null;
    }

    public static String getInternalStorage() {
        String dataPath = existFile("/data/media/0", true) ? "/data/media/0" : "/data/media";
        if (!new RootFile(dataPath).isEmpty()) {
            return dataPath;
        }
        if (existFile("/sdcard", true)) {
            return "/sdcard";
        }
        return Environment.getExternalStorageDirectory().getPath();
    }

    public static String calculateMD5(File updateFile) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "Exception while getting digest", e);
            return null;
        }

        InputStream is;
        try {
            is = new FileInputStream(updateFile);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Exception while getting FileInputStream", e);
            return null;
        }

        byte[] buffer = new byte[8192];
        int read;
        try {
            while ((read = is.read(buffer)) > 0) {
                digest.update(buffer, 0, read);
            }
            byte[] md5sum = digest.digest();
            BigInteger bigInt = new BigInteger(1, md5sum);
            String output = bigInt.toString(16);
            // Fill to 32 chars
            output = String.format("%32s", output).replace(' ', '0');
            return output;
        } catch (IOException e) {
            throw new RuntimeException("Unable to process file for MD5", e);
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                Log.e(TAG, "Exception on closing MD5 input stream", e);
            }
        }
    }

    public static void toast(String message, Context context) {
        toast(message, context, Toast.LENGTH_SHORT);
    }

    public static void toast(@StringRes int id, Context context) {
        toast(context.getString(id), context);
    }

    public static void toast(String message, Context context, int duration) {
        Toast.makeText(context, message, duration).show();
    }

    public static boolean existFile(String file) {
        return existFile(file, true);
    }

    private static boolean existFile(String file, boolean root) {
        return root ? new RootFile(file).exists() : new File(file).exists();
    }

}
