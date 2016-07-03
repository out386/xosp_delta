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
    final static String TAG = "XOSPDelta";

    final static String ACTION_CLOSE_DIALOG = "delta.out386.xosp.CLOSE_DIALOG";
    final static String ACTION_APPLY_DIALOG = "delta.out386.xosp.APPLY_DIALOG";
	final static String ACTION_APPLY_DIALOG_FIRST_START = "delta.out386.xosp.APPLY_DIALOG_FIRST_START";
    final static String GENERIC_DIALOG = "delta.out386.xosp.GENERIC_DIALOG";
	final static String GENERIC_DIALOG_FIRST_START = "delta.out386.xosp.GENERIC_DIALOG_FIRST_START";
    final static String PROGRESS_DIALOG = "delta.out386.xosp.PROGRESS_DIALOG";
    final static String ACTION_NOT_XOSP_DIALOG = "delta.out386.xosp.NOT_XOSP_DIALOG";
	final static String AUTO_UPDATE = "delta.out386.xosp.AUTO_UPDATE_DIALOG";
	final static String NO_ROMS = "delta.out386.xosp.NO_ROMS";
	
    final static String PROGRESS = "delta.out386.xosp.PROGRESS";
    final static String DIALOG_MESSAGE = "delta.out386.xosp.DIALOG_MESSAGE";
    final static String GENERIC_DIALOG_MESSAGE = "delta.out386.xosp.GENERIC_DIALOG_MESSAGE";
	final static String AUTO_UPDATE_BASE = "delta.out386.xosp.AUTO_UPDATE_BASE";
    final static String AUTO_UPDATE_DELTA = "delta.out386.xosp.AUTO_UPDATE_DELTA";

    /**
     * Information about the supported rom.
     * SUPPORTED_ROM_PROP is the is the property that XOSP uses to identify itself.
     * SUPPORTED_ROM_PROP_NAME is any unique part of the SUPPORTED_ROM_PROP property.
     */

    final static String SUPPORTED_ROM_FULL_NAME = "Xperia Open Source Project";
    final static String SUPPORTED_ROM_PROP = "ro.xosp.display.version";
    final static String SUPPORTED_ROM_PROP_NAME="XOSP";
	
	// The delimiter(s) used in the ROM zip to separate name, date, version, etc. 
    // Include "." in delimiter, adjust LOCATION constants appropriately
    final static String ROM_ZIP_DELIMITER = "-.";
    final static int ROM_ZIP_NAME_LOCATION = 1;
    final static int ROM_ZIP_DATE_LOCATION = 5;
    final static int ROM_ZIP_DEVICE_LOCATION = 6;
    final static String ROM_ZIP_NAME = "XOSP";
    final static String ROM_ZIP_DEVICE_NAME = Shell.SH.run("getprop ro.xosp.device").get(0);

    final static String [] OFFICIAL_LIST = {
            "angler", "armani", "d851", "d855", "falcon", "h811", "h815", "hammerhead",
            "lettuce", "lux", "mako", "oneplus2", "osprey", "shamu", "sprout4", "sprout8",
            "surnia", "titan", "tomato", "vs985", "Z008", "Z00A"
    };

    /**
     * EXAMPLE : ROMName-VersionMajor.VersionMinor-OFFICIAL-Date-Device.zip
     */
	 
	 final static String UPDATE_JSON_URL = "https://basketbuild.com/api4web/devs/XOSP/";
}
