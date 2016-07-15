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

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import delta.out386.xosp.JenkinsJson.builds;
import eu.chainfire.libsuperuser.Shell;

public class Tools {
    public static String sizeFormat(long size) {
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
            unit = " Your phone is insanE(iB)";
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
        catch (NumberFormatException e) {
            try {
                romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION_2 - indexOffset]);
            }
            catch (NumberFormatException e2) {
                romDate.date = -1;
            }
        }

        // Rough date after which deltas were introduced in XOSP
        if(romDate.date < 20160501)
            romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION_2 - indexOffset]);

        if(moreInfo) {
            romDate.romName = fileComponents[Constants.ROM_ZIP_NAME_LOCATION - indexOffset];
            romDate.deviceName = fileComponents[Constants.ROM_ZIP_DEVICE_LOCATION - indexOffset];
            if(! romDate.deviceName.equals(Constants.ROM_ZIP_DEVICE_NAME))
                romDate.deviceName = fileComponents[Constants.ROM_ZIP_DEVICE_LOCATION_2 - indexOffset];
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

        int installedBuildDate = romZipDate(
                Shell.SH.run("getprop " + Constants.SUPPORTED_ROM_PROP)
                        .get(0), false)
                .date;
        int newestBuildDate = 0;
        for(JenkinsJson.builds currentBuild : updates.builds) {
            if (currentBuild.artifacts.length > 0) {
                newestBuildDate = romZipDate(currentBuild.artifacts[0].fileName, false).date;
                break;
            }
        }

        if(installedBuildDate == -1 || newestBuildDate == -1)
        {
            Log.i(Constants.TAG, "Kill the guy who changed the filename. Malformed ROM name.");
            sendGenericToast("Malformed ROM name. Please contact your devices' maintainer.", context);
            return false;
        }

        // As the update to installedBuildDate will have the same number
        if(installedBuildDate > newestBuildDate) {
            // No updates needed
            return false;
        }

        Flashables newestDownloadedDelta = findNewestDownloadedDelta(context);
        int downloadedBuildDate = 0;
        if(newestDownloadedDelta != null)
             downloadedBuildDate = romZipDate(newestDownloadedDelta.file.getName(), false).date;

        if(downloadedBuildDate == -1)
        {
            Log.i(Constants.TAG, "Kill the guy who changed the file name. Malformed ROM name.");
            sendGenericToast("Malformed ROM name. Please contact your devices' maintainer.", context);
            return false;
        }

        if(downloadedBuildDate > newestBuildDate)
            return false;

        int newestAlreadyPresent = (installedBuildDate > downloadedBuildDate) ? installedBuildDate : downloadedBuildDate;
        Log.i(Constants.TAG, "newestAlreadyPresent " + newestAlreadyPresent);
        /* The builds info returned by the Jenkins API is already sorted in a reverse
         * order. We need the oldest build to be the first element.
         */
        Collections.reverse(updates.builds);
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
            currentBuild.artifacts[0].downloadUrl = Constants.UPDATE_JSON_URL_JENKINS_1
                    + Constants.ROM_ZIP_DEVICE_NAME + ")/"
                    + currentBuild.id
                    + "/artifact/"
                    + currentBuild.artifacts[0].relativePath;
            Date tempdate = new Date(currentBuild.timestamp);
            currentBuild.date = Integer.parseInt(new SimpleDateFormat("yyyyMMdd").format(tempdate));
            currentBuild.stringDate = new SimpleDateFormat("MMM dd yyyy").format(tempdate);

            if(currentBuild.date <= newestAlreadyPresent) {
                Log.i(Constants.TAG, ""+currentBuild.date);
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
            currentIndex++;
        }

        int numberElementsRemoved = 0;
        for(int removeCurrent = 0; removeCurrent <= removeIndex - 1; removeCurrent++)
            updates.builds.remove(remove[removeCurrent] - numberElementsRemoved++);
        if(updates.builds.size() > 0)
            return true;
        return false;
    }

    public static Flashables findNewestDownloadedDelta(Context context) {
        List<Flashables> storedRoms = new FindZips(context, null, context.getSharedPreferences("settings", Context.MODE_PRIVATE))
                .run()
                .deltas;
        if(storedRoms.size() == 0)
            return null;
        Collections.sort(storedRoms, new Comparator<Flashables>() {
            @Override
            public int compare(Flashables o1, Flashables o2) {
                return -(o1.file.getName().compareTo(o2.file.getName()));
            }
        });
        return storedRoms.get(0);
    }

    public static void sendGenericToast(String message, Context context) {
        Intent genericToast = new Intent(Constants.GENERIC_TOAST);
        genericToast.putExtra(Constants.GENERIC_TOAST_MESSAGE, message);
        LocalBroadcastManager.getInstance(context).sendBroadcast(genericToast);
    }
}
