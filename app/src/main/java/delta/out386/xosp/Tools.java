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

import android.util.Log;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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
        int indexOffset;
        RomDateType romDate = new RomDateType();
        String[] fileComponents = romName.split("[" + Constants.ROM_ZIP_DELIMITER + "]");

        // As deltas will have all their indices offset one place to the right; indices already start from 1.
        if(fileComponents[0].equals("delta")) {
            indexOffset = 0;
            romDate.isDelta = true;
        }
        else
            indexOffset = 1;
        try {
            romDate.date = Integer.parseInt(fileComponents[Constants.ROM_ZIP_DATE_LOCATION - indexOffset]);
        }
        catch (NumberFormatException e) {
            romDate.date = -1;
        }

        if(moreInfo) {
            romDate.romName = fileComponents[Constants.ROM_ZIP_NAME_LOCATION - indexOffset];
            romDate.deviceName = fileComponents[Constants.ROM_ZIP_DEVICE_LOCATION - indexOffset];
        }
        return romDate;
    }
    public static class RomDateType {
        boolean isDelta;
        int date;
        String romName, deviceName;
    }

    public static boolean processJenkins(JenkinsJson updates) {
        /* Here, each build (Jenkins "job") has just one artifact. That's just how the server's set up.
         * That is why the loop iterates over "builds" and not over both "builds" and "artifacts".
         * This behaviour may or may not be changed later.
         */
        int buildsSize = updates.builds.size();
        if(buildsSize == 0)
            return false;
        int  removeIndex = 0, currentIndex = 0;
        int [] remove = new int [updates.builds.size()];

        int installedBuildDate = Tools.romZipDate(
                Shell.SH.run("getprop " + Constants.SUPPORTED_ROM_PROP)
                        .get(0), false)
                .date;
        int newestBuildDate = 0;
        for(JenkinsJson.builds currentBuild : updates.builds) {
            if (currentBuild.artifacts.length > 0) {
                newestBuildDate = Tools.romZipDate(currentBuild.artifacts[0].fileName, false).date;
                break;
            }
        }
        if(installedBuildDate >= newestBuildDate) {
            // No updates needed
            return false;
        }

        /* The builds info returned by the Jenkins API is already sorted in a reverse
         * order. We need the oldest build to be the first element.
         */
        Collections.reverse(updates.builds);
        for(builds currentBuild : updates.builds) {
            // Removing duplicate jobs. Just in case.

            if(currentBuild.artifacts.length == 0) {
                remove[removeIndex++] = currentIndex++;
                continue;
            }
            for(int i = currentIndex + 1; i < buildsSize; i++) {
                try {
                    if (currentBuild.fingerprint[0].hash.equals(updates.builds.get(i).fingerprint[0].hash))
                        if(Arrays.binarySearch(remove, 0, removeIndex, i) < 0)
                            remove[removeIndex++] = i;
                } catch (ArrayIndexOutOfBoundsException e) {
                    // Just means that it found a job with no fingerprints
                }
            }
            if(currentBuild.artifacts.length != 0) {
                // This will calculate the date even for duplicates. To fix.
                RomDateType romType = romZipDate(currentBuild.artifacts[0].fileName, false);
                if(romType.date == -1) {
                    updates.isMalformed = true;
                    return false;
                }
                    currentBuild.artifacts[0].date = romType.date;
                    currentBuild.artifacts[0].isDelta = romType.isDelta;
            }
            currentIndex++;
        }

        int numberElementsRemoved = 0;
        for(int removeCurrent = 0; removeCurrent <= removeIndex - 1; removeCurrent++)
            updates.builds.remove(remove[removeCurrent] - numberElementsRemoved++);
        return true;
    }
}
