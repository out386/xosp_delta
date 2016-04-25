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

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;

public class BuiltSizeService extends IntentService {
    final String TAG = Constants.TAG;
    public BuiltSizeService(){
        super("SizeService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String target = intent.getStringExtra("target");
        long targetSize = intent.getLongExtra("targetSize", 0), currentSize = 0;
        final int INTERVAL = 2;
        boolean isFileReady = false;
        Intent progress = new Intent(Constants.PROGRESS_DIALOG);
        while(!isFileReady || currentSize < targetSize) {
            File targetFile = new File(target);
            if(targetFile.exists()) {
                isFileReady = true;
                currentSize = targetFile.length();
                int progressValue = (int) ((float)currentSize/targetSize * 100);
                progress.putExtra(Constants.PROGRESS, progressValue);
                LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(progress);
                try {
                    Thread.sleep(INTERVAL * 1000);
                }
                catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }
}
