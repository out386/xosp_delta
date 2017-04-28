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
 
package delta.out386.xosp;
import eu.chainfire.libsuperuser.Shell;

public class Constants {
    final public static String TAG = "XOSPDelta";

    final static String ACTION_CLOSE_DIALOG = "delta.out386.xosp.CLOSE_DIALOG";
    final static String ACTION_APPLY_DIALOG = "delta.out386.xosp.APPLY_DIALOG";
	final static String ACTION_APPLY_DIALOG_FIRST_START = "delta.out386.xosp.APPLY_DIALOG_FIRST_START";
    final static String GENERIC_DIALOG = "delta.out386.xosp.GENERIC_DIALOG";
	final static String GENERIC_DIALOG_FIRST_START = "delta.out386.xosp.GENERIC_DIALOG_FIRST_START";
    final static String PROGRESS_DIALOG = "delta.out386.xosp.PROGRESS_DIALOG";
    final static String ACTION_NOT_XOSP_DIALOG = "delta.out386.xosp.NOT_XOSP_DIALOG";
	final static String AUTO_UPDATE = "delta.out386.xosp.AUTO_UPDATE_DIALOG";
	final static String NO_ROMS = "delta.out386.xosp.NO_ROMS";
    final static String JSON_AVAILABILITY = "delta.out386.xosp.JSON_AVAILABILITY";
    final static String IS_JSON_AVAILABLE = "delta.out386.xosp.IS_JSON_AVAILABLE";
	
    final static String PROGRESS = "delta.out386.xosp.PROGRESS";
    final static String DIALOG_MESSAGE = "delta.out386.xosp.DIALOG_MESSAGE";
    final static String GENERIC_DIALOG_MESSAGE = "delta.out386.xosp.GENERIC_DIALOG_MESSAGE";
	final static String AUTO_UPDATE_BASE = "delta.out386.xosp.AUTO_UPDATE_BASE";
    final static String AUTO_UPDATE_DELTA = "delta.out386.xosp.AUTO_UPDATE_DELTA";

    final static String GENERIC_TOAST = "delta.out386.xosp.GENERIC_TOAST";
    final static String GENERIC_TOAST_MESSAGE = "delta.out386.xosp.GENERIC_TOAST_MESSAGE";

    final static String PENDING_DOWNLOADS_INTENT = "delta.out386.xosp.PENDING_DOWNLOADS_INTENT";
    final static String PENDING_DOWNLOADS = "delta.out386.xosp.PENDING_DOWNLOADS";

    final static String DOWNLOADS_JSON = "delta.out386.xosp.DOWNLOADS_JSON";
    final static String DOWNLOADS_INTENT = "delta.out386.xosp.DOWNLOADS_INTENT";
    final static String DOWNLOADS_DONE_INTENT = "delta.out386.xosp.DOWNLOADS_DONE_INTENT";
    final static String DOWNLOADS_PROGRESS = "delta.out386.xosp.DOWNLOADS_PROGRESS";
    final static String DOWNLOADS_PROGRESS_VALUE = "delta.out386.xosp.DOWNLOADS_PROGRESS_VALUE";
    final static String DOWNLOADS_PROGRESS_ID = "delta.out386.xosp.DOWNLOADS_PROGRESS_ID";

    final static String SUPPORTED_ROM_FULL_NAME = "Xperia Open Source Project";

    /**
     * Information about the supported rom.
     * SUPPORTED_ROM_PROP is the is the property that XOSP uses to identify itself.
     * SUPPORTED_ROM_PROP_NAME is any unique part of the SUPPORTED_ROM_PROP property.
     */
    final static String SUPPORTED_ROM_PROP = "ro.xosp.display.version";
    /**
     * EXAMPLE : ROMName-VersionMajor.VersionMinor-OFFICIAL-Date-Device
     *           ROMName-VersionMajor.VersionMinor-FINAL-MM-OFFICIAL-Date-Device
     * Date is assumed to be in the format YYYYMMDD
     */

    final static String SUPPORTED_ROM_PROP_NAME="XOSP";
	
	// The delimiter(s) used in the ROM zip to separate name, date, version, etc. 
    // Include "." in delimiter, adjust LOCATION constants appropriately
    final static String ROM_ZIP_DELIMITER = "-.";
    final static int ROM_ZIP_NAME_LOCATION = 1;
    final static int ROM_ZIP_DATE_LOCATION = 5;

    // Used if the naming format changes (Which it did, for XOSP)
    final static int ROM_ZIP_DATE_LOCATION_2 = 3;
    final static int ROM_ZIP_DEVICE_LOCATION = 6;
    final static int ROM_ZIP_DEVICE_LOCATION_2 = 4;
    final static String ROM_ZIP_NAME = "XOSP";
    final static String ROM_ZIP_DEVICE_NAME = Shell.SH.run("getprop ro.xosp.device").get(0);


    final static String [] OFFICIAL_LIST = {
           "Z008", "Z00A", "angler", "armani", "huashan", "kenzo", "lux", "mako", "onyx", "surnia", "osprey", "libra"
    };
	 
	final static String UPDATE_JSON_URL_BASKETBUILD1 = "https://basketbuild.com/api4web/devs/XOSP/";
    final static String UPDATE_JSON_URL_BASKETBUILD2 = "/deltas";
    final static String DOWNLOAD_FILE_BASKETBUILD1 = "https://basketbuild.com/uploads/devs/XOSP/";
    final static String DOWNLOAD_FILE_BASKETBUILD2 = "/deltas/";
    final static String UPDATE_JSON_URL_JENKINS_1 = "http://144.76.38.141:8090/job/XOSPWeeklies(";
    final static String UPDATE_JSON_URL_JENKINS_2 = ")/api/json?depth=1&tree=builds[artifacts[fileName,relativePath],id,timestamp,fingerprint[hash]]";
    final static String DOWNLOADS_API_TYPE_BASKETBUILD = "delta.out386.xosp.DOWNLOADS_API_TYPE_BASKETBUILD";
    final static String DOWNLOADS_API_TYPE_JENKINS = "delta.out386.xosp.DOWNLOADS_API_TYPE_JENKINS";
    final static String CURRENT_DOWNLOADS_API_TYPE = DOWNLOADS_API_TYPE_BASKETBUILD;
    final static String CONNECTIVITY_CHECK_URL = "http://connectivitycheck.gstatic.com/generate_204";

    /* Link to a file containing just the name of the newest ROM.
     * Hosted on a server that will be accessible even if the main downloads server is not.
     * Used so that the delta can be downloaded manually from an alternate server if the main one is down.
     */
    final static String NEWEST_BUILD_URL_ALT = "https://raw.githubusercontent.com/XOSP-Project/utilities-xosp-changelogs/master/" + ROM_ZIP_DEVICE_NAME + "/Current-Version.txt";

}
