/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/* This file is part of XOSPDelta.
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

package delta.out386.xosp;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;

import javax.net.ssl.HttpsURLConnection;

import delta.out386.xosp.JenkinsJson.builds;
import eu.chainfire.libsuperuser.Shell;

public class Tools {
    public static String sizeFormat(long size) {
        if(size <= 0)
            return null;
        float newSize = size;
        String unit = " B";
        if (newSize > 1024) {
            unit = " KiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " MiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " GiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = " TiB";
            newSize = newSize / 1024;
        }
        if (newSize >= 1024) {
            unit = ". Corrupt volume.";
            newSize = 0;
        }
        return (new DecimalFormat("#0.00").format(newSize) + unit);
    }
    public static RomDateType romZipDate(String romName, boolean moreInfo) {
        int indexOffset = 1;
        RomDateType romDate = new RomDateType();
        String[] fileComponents = romName.split("[" + Constants.ROM_ZIP_DELIMITER + "]");

        // As deltas will have all their indices offset one place to the right; indices already start from 1.
        if(fileComponents[0].equals("delta")) {
            indexOffset = 0;
            romDate.isDelta = true;
        }

        try {
            romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION - indexOffset]);
        }
        catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            try {
                romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION_2 - indexOffset]);
            }
            catch (NumberFormatException | ArrayIndexOutOfBoundsException e2) {
                romDate.date = -1;
                Log.e(Constants.TAG, "romZipDate: " + "Rom naming scheme has changed.");
                return romDate;
            }
        }

        // Rough date after which deltas were introduced in XOSP
        /*if(romDate.date < 20160501)
            romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION_2 - indexOffset]);*/

        if(moreInfo) {
            if (fileComponents.length > (Constants.ROM_ZIP_NAME_LOCATION - indexOffset)
                && fileComponents.length > (Constants.ROM_ZIP_DEVICE_LOCATION - indexOffset)) {
                romDate.romName = fileComponents[Constants.ROM_ZIP_NAME_LOCATION - indexOffset];
                romDate.deviceName = fileComponents[Constants.ROM_ZIP_DEVICE_LOCATION - indexOffset];
            }
            if(! Constants.ROM_ZIP_DEVICE_NAME.equals(romDate.deviceName)
                && fileComponents.length > (Constants.ROM_ZIP_DEVICE_LOCATION_2 - indexOffset)
                && fileComponents.length > (Constants.ROM_ZIP_NAME_LOCATION - indexOffset)) {
                romDate.deviceName = fileComponents[Constants.ROM_ZIP_DEVICE_LOCATION_2 - indexOffset];
                romDate.romName = fileComponents[Constants.ROM_ZIP_NAME_LOCATION - indexOffset];
                // Assuming rom name location is not changed in the 2nd naming scheme
                // To-Do: Check if alternate location should be used, or if user is using the zip of another device
            }
        }
        return romDate;
    }
    public static class RomDateType {
        boolean isDelta;
        int date;
        String romName, deviceName;
    }

    public static boolean processJenkins(JenkinsJson updates, Context context) {
        /* Here, each build (Jenkins "job") has just one artifact. That's just how the server's set up.
         * That is why the loop iterates over "builds" and not over both "builds" and "artifacts".
         * This behaviour may or may not be changed later.
         */
        int buildsSize = updates.builds.size();
        if(buildsSize == 0)
            return false;
        int  removeIndex = 0, currentIndex = 0;
        int [] remove = new int [updates.builds.size()];

        int installedBuildDate = romZipDate(getInstalledRomName(), false).date;
        int newestBuildDate = 0;
        for(JenkinsJson.builds currentBuild : updates.builds) {
            if (currentBuild.artifacts.length > 0) {
                newestBuildDate = romZipDate(currentBuild.artifacts[0].fileName, false).date;
                break;
            }
        }

        if(installedBuildDate == -1 || newestBuildDate == -1)
        {
            Log.e(Constants.TAG, "Kill the guy who changed the filename. Malformed ROM name.");
            sendGenericToast("Malformed ROM name. Please contact your devices' maintainer.", context);
            return false;
        }

        // As the update to installedBuildDate will have the same number
        if(installedBuildDate > newestBuildDate) {
            // No updates needed
            return false;
        }

        FlashablesTypeList newestDownloadedZip = findNewestDownloadedZip(context, true);
        int downloadedRomBuildDate = 0, downloadedDeltaBuildDate = 0;
        if(newestDownloadedZip != null) {
            if(newestDownloadedZip.deltas.size() != 0)
                downloadedDeltaBuildDate = romZipDate(newestDownloadedZip.deltas.get(0).file.getName(), false).date;
            if(newestDownloadedZip.roms.size() != 0)
                downloadedRomBuildDate = romZipDate(newestDownloadedZip.roms.get(0).file.getName(), false).date;
        }

        if(downloadedRomBuildDate == -1 || downloadedDeltaBuildDate == -1)
        {
            Log.e(Constants.TAG, "Kill the guy who changed the file name. Malformed ROM name.");
            sendGenericToast("Malformed ROM name. Please contact your devices' maintainer.", context);
            return false;
        }

        if(downloadedRomBuildDate > newestBuildDate)
            return false;

        if(Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_JENKINS){
        /* The builds info returned by the Jenkins API is already sorted in a reverse
         * order. We need the oldest build to be the first element.
         */
            Collections.reverse(updates.builds);
        }

        for(builds currentBuild : updates.builds) {

            // Removing empty jobs
            if(currentBuild.artifacts.length == 0) {
                remove[removeIndex++] = currentIndex++;
                continue;
            }

            // This will calculate the date even for duplicates. To fix.
            RomDateType romType = romZipDate(currentBuild.artifacts[0].fileName, false);
            if(romType.date == -1) {
                updates.isMalformed = true;
                return false;
            }
            currentBuild.artifacts[0].date = romType.date;
            currentBuild.artifacts[0].isDelta = romType.isDelta;
            Date tempdate;
            if( Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_BASKETBUILD)
                tempdate = new Date(currentBuild.timestamp * 1000L);
            else if(Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_JENKINS)
                tempdate = new Date(currentBuild.timestamp);


            currentBuild.stringDate = new SimpleDateFormat("MMM dd yyyy").format(tempdate);

            if(currentBuild.artifacts.length > 0)
                if(currentBuild.artifacts[0].date < downloadedRomBuildDate || currentBuild.artifacts[0].date < installedBuildDate) {
                    Log.i(Constants.TAG, "removing "+currentBuild.artifacts[0].date);
                    remove[removeIndex++] = currentIndex++;
                    continue;
                }

            for(int i = currentIndex + 1; i < buildsSize; i++) {
                try {
                    // Removing duplicate jobs. Just in case.
                    if (currentBuild.fingerprint[0].hash.equals(updates.builds.get(i).fingerprint[0].hash))
                        if(Arrays.binarySearch(remove, 0, removeIndex, i) < 0)
                            remove[removeIndex++] = i;
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Just means that it found a job with no fingerprints
                }
            }

            if(newestDownloadedZip != null) {
                /* Removing deltas that have already been downloaded.
                 * This is needed because it is possible that even if the newest delta
                 * is present, an older delta might be missing.
                 */
                boolean foundAlreadyDownloaded = false;
                for (Flashables current : newestDownloadedZip.deltas) {
                    if (currentBuild.artifacts[0].fileName.equals(current.file.getName())) {
                        remove[removeIndex++] = currentIndex++;
                        foundAlreadyDownloaded = true;
                        break;
                    }
                }
                if(foundAlreadyDownloaded)
                    continue;
            }
            currentIndex++;
        }

        int numberElementsRemoved = 0;
        for(int removeCurrent = 0; removeCurrent <= removeIndex - 1; removeCurrent++)
            updates.builds.remove(remove[removeCurrent] - numberElementsRemoved++);
        if(updates.builds.size() == 0)
            return false;

        if(Constants.CURRENT_DOWNLOADS_API_TYPE == Constants.DOWNLOADS_API_TYPE_JENKINS) {
            for (builds currentBuild : updates.builds) {
                currentBuild.artifacts[0].downloadUrl = Constants.UPDATE_JSON_URL_JENKINS_1
                        + Constants.ROM_ZIP_DEVICE_NAME + ")/"
                        + currentBuild.id
                        + "/artifact/"
                        + currentBuild.artifacts[0].relativePath;
                String size = sizeFormat(getUrlSize(currentBuild.artifacts[0].downloadUrl));
                if (size != null)
                    currentBuild.artifacts[0].size = size;
                else
                    currentBuild.artifacts[0].size = "Size unavailable";
            }
        }
        return true;
    }

    public static FlashablesTypeList findNewestDownloadedZip(Context context, boolean allDeltas) {
        FlashablesTypeList zips = new FindZips(context, null, context.getSharedPreferences("settings", Context.MODE_PRIVATE))
                .run();
        List<Flashables> storedDeltas = zips.deltas;
        List<Flashables> storedRoms = zips.roms;
        FlashablesTypeList output = new FlashablesTypeList();
        if(storedDeltas.size() != 0) {
            Collections.sort(storedDeltas, new Comparator<Flashables>() {
                @Override
                public int compare(Flashables o1, Flashables o2) {
                    return -(o1.file.getName().compareTo(o2.file.getName()));
                }
            });
            if(allDeltas) {
                for(Flashables current : storedDeltas)
                    output.addFlashable(current);
            }
            else
                output.addFlashable(storedDeltas.get(0));
        }

        if(storedRoms.size() != 0) {
            Collections.sort(storedRoms, new Comparator<Flashables>() {
                @Override
                public int compare(Flashables o1, Flashables o2) {
                    return -(o1.file.getName().compareTo(o2.file.getName()));
                }
            });
            output.addFlashable(storedRoms.get(0));
        }

        return output;
    }

    public static void sendGenericToast(String message, Context context) {
        Intent genericToast = new Intent(Constants.GENERIC_TOAST);
        genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
    }

    public static int getUrlSize(String url) {
        // int can hold sizes of over 1.8GiB; let's hope that no one makes a ROM that big.
        URLConnection connection;
        try {
            connection = new URL(url).openConnection();
            return connection.getContentLength();
        }
        catch(Exception e) {
            // Let Medescope take care of this
        }
        return -1;
    }

    public static boolean checkHost(String host) {
        try {
            int response;
        HttpURLConnection connection = (HttpURLConnection) new URL(host).openConnection();
            connection.setRequestMethod("HEAD");
            if((response = connection.getResponseCode()) == 204) {
                return true;
            }
            else {
                Log.i(Constants.TAG, "No internet: " + response);
                return false;
            }
        } catch(Exception e) {
            Log.i(Constants.TAG, "No internet: " + e);
            return false;
        }
    }
    public static String getInstalledRomName() {
        return Shell.SH.run("getprop " + Constants.SUPPORTED_ROM_PROP)
                .get(0);
    }
    public static boolean isNewRomAvailable(FlashablesTypeList downloadedZips) {
        // Is a new ROM available for flashing?
        // Does not refer to a new ROM/delta that needs to be downloaded

        boolean isAvailable = false;
        if(downloadedZips != null && downloadedZips.roms.size() > 0) {
            isAvailable = romZipDate(downloadedZips.roms.get(0).file.getName(), false).date > romZipDate(getInstalledRomName(), false).date;
            if (downloadedZips.deltas.size() != 0) {
                isAvailable = isAvailable && ! foundRomForOldestDelta(downloadedZips);
                // If foundRomForOldestDelta returns true, then that means that a delta needs to be applied first.
            }
        }
        return isAvailable;
    }
    public static String romFromDeltaName(String deltaName) {
        return deltaName.substring(deltaName.indexOf('.') + 1);
    }
    public static boolean foundRomForOldestDelta(FlashablesTypeList downloadedZips) {
        for(Flashables currentRom : downloadedZips.roms) {
            // Checking whether the base ROM for the oldest delta exists
            if(downloadedZips.deltas.size() > 0)
                if(currentRom.file.getName().equals(romFromDeltaName(downloadedZips.deltas.get(downloadedZips.deltas.size() -1).file.getName())))
                    return true;
        }
        return false;
    }
}
