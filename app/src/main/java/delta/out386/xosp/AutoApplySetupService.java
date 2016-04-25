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
import java.util.StringTokenizer;

public class AutoApplySetupService extends IntentService {
    final String TAG = Constants.TAG;
    Intent messageDialog = new Intent(Constants.GENERIC_DIALOG_MESSAGE),
            autoUpdate = new Intent(Constants.AUTO_UPDATE),
            noRoms = new Intent(Constants.NO_ROMS);
    public AutoApplySetupService(){
        super("AutoApplySetupService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        FlashablesTypeList flashablesList = new FindZips(getApplication(), true, null).run();
        String romName = null, deviceName = null;
        int date = 0, maxDate = 0, location = 1;
        File newestRom = null;
        if(flashablesList == null || flashablesList.roms == null || flashablesList.roms.size() == 0) {
            LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(noRoms);
            return;
        }
        for(Flashables current : flashablesList.roms) {
            Log.v(TAG,"Foreach");
            StringTokenizer st = new StringTokenizer(current.file.getName(), Constants.ROM_ZIP_DELIMITER);
            while(st.hasMoreTokens()) {
                Log.v(TAG,"while");
                switch(location) {
                    case Constants.ROM_ZIP_NAME_LOCATION: romName = st.nextToken();
                        break;
                    case Constants.ROM_ZIP_DATE_LOCATION: try {
                        date = Integer.parseInt(st.nextToken());
                        }
                        catch(NumberFormatException e) {}
                        break;
                    case Constants.ROM_ZIP_DEVICE_LOCATION: deviceName = st.nextToken();
                        break;
                    default:
                        Log.v(TAG, "token  :  " + st.nextToken());
                        break;
                }
                Log.v(TAG, "location  :  " +location);
                location++;
                Log.v(TAG,"Name " + romName);
                Log.v(TAG,"Device " + deviceName);
                Log.v(TAG,"date " + date);
                if(romName == null || deviceName == null)
                    continue;
                if(deviceName.equalsIgnoreCase(Constants.ROM_ZIP_DEVICE_NAME))
                    if(romName.equalsIgnoreCase(Constants.ROM_ZIP_NAME))
                        if(date > maxDate) {
                            maxDate = date;
                            newestRom = current.file;
                            break;
                        }
            }
            location = 1;
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
        //messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "No updates needed");
        Log.v(TAG, "No updates needed");
        // Get the fake dialog up
        /*Intent noUpdateIntent = new Intent(this, DeltaDialogActivity.class)
                .putExtra(Constants.GENERIC_DIALOG_MESSAGE, "No updates needed.");
        startActivity(noUpdateIntent);*/
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(messageDialog.putExtra(Constants.GENERIC_DIALOG_MESSAGE, "No updates needed"));

        /**
         *  Delay needed as the dialog activity needs time to register
         * the broadcast receiver
         */
        /*try {
            Thread.sleep(500);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
        LocalBroadcastManager.getInstance(getApplication()).sendBroadcast(messageDialog);*/
    }
}
