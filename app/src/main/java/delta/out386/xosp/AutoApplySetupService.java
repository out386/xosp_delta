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
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.util.StringTokenizer;

public class AutoApplySetupService extends IntentService {
    final String TAG = Constants.TAG;
    Intent autoUpdate = new Intent(Constants.AUTO_UPDATE),
            noRoms = new Intent(Constants.NO_ROMS);
    public AutoApplySetupService(){
        super("AutoApplySetupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        FlashablesTypeList flashablesList = new FindZips(getApplication(), null, getSharedPreferences("settings", Context.MODE_PRIVATE)).run();
        String romName, deviceName;
        int date, maxDate = 0;
        File newestRom = null;
        if(flashablesList == null || flashablesList.roms == null || flashablesList.roms.size() == 0) {
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(noRoms);
            return;
        }
        for(Flashables current : flashablesList.roms) {
            Tools.RomDateType romInfo = Tools.romZipDate(current.file.getName(), true);
            romName = romInfo.romName;
            date = romInfo.date;
            deviceName = romInfo.deviceName;
            Log.v(TAG,"Name " + romName);
            Log.v(TAG,"Device " + deviceName);
            Log.v(TAG,"date " + date);
            if(romName == null || deviceName == null)
                continue;
            if(deviceName.equals(Constants.ROM_ZIP_DEVICE_NAME))
                if(romName.equals(Constants.ROM_ZIP_NAME))
                    if(date > maxDate) {
                        maxDate = date;
                        newestRom = current.file;
                    }

        }
        if(newestRom == null) {
            Log.v(TAG, "No updates needed");
            noUpdate();
            return;
        }
        String deltaZipName;
        deltaZipName = newestRom.getParent() + "/delta." + newestRom.getName();
        Log.v(TAG, deltaZipName);
        File deltaZip = new File(deltaZipName);
        Log.v(TAG, newestRom.toString());

        autoUpdate.putExtra(Constants.AUTO_UPDATE_BASE, new Flashables(newestRom, "rom", newestRom.length()));
        autoUpdate.putExtra(Constants.AUTO_UPDATE_DELTA, new Flashables(deltaZip, "delta", deltaZip.length()));
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(autoUpdate);

        if(! deltaZip.exists()) {
            noUpdate();
        }
    }
    private void noUpdate(){
        Log.v(TAG, "No updates needed");
        // ReadFlashablesQueue will make the card with the no update message visible
    }
}
