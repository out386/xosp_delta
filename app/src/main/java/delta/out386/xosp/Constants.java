package delta.out386.xosp;

public class Constants {
    final static String TAG = "XOSPDelta";

    final static String ACTION_CLOSE_DIALOG = "delta.out386.xosp.CLOSE_DIALOG";
    final static String ACTION_APPLY_DIALOG = "delta.out386.xosp.APPLY_DIALOG";
    final static String GENERIC_DIALOG = "delta.out386.xosp.GENERIC_DIALOG";
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
    // Include "." in delimiter, adjust LOCATION constants appropiately
    final static String ROM_ZIP_DELIMITER = "-.";
    final static int ROM_ZIP_NAME_LOCATION = 1;
    final static int ROM_ZIP_DATE_LOCATION = 5;
    final static int ROM_ZIP_DEVICE_LOCATION = 6;
    final static String ROM_ZIP_NAME = "XOSP";
    final static String ROM_ZIP_DEVICE_NAME = "Z00A";
    /**
     * EXAMPLE : ROMName-VersionMajor.VersionMinor-OFFICIAL-Date-Device.zip
     */
}
