package delta.out386.xosp;

public class Constants {
    final static String TAG = "XOSPDelta";

    final static String ACTION_CLOSE_DIALOG = "delta.out386.xosp.CLOSE_DIALOG";
    final static String ACTION_APPLY_DIALOG = "delta.out386.xosp.APPLY_DIALOG";
    final static String GENERIC_DIALOG = "delta.out386.xosp.GENERIC_DIALOG";
    final static String PROGRESS_DIALOG = "delta.out386.xosp.PROGRESS_DIALOG";
    final static String ACTION_NOT_XOSP_DIALOG = "delta.out386.xosp.NOT_XOSP_DIALOG";

    final static String PROGRESS = "delta.out386.xosp.PROGRESS";
    final static String DIALOG_MESSAGE = "delta.out386.xosp.DIALOG_MESSAGE";
    final static String GENERIC_DIALOG_MESSAGE = "delta.out386.xosp.GENERIC_DIALOG_MESSAGE";

    /**
     * Information about the supported rom.
     * SUPPORTED_ROM_PROP is the is the property that XOSP uses to identify itself.
     * SUPPORTED_ROM_PROP_NAME is any unique part of the SUPPORTED_ROM_PROP property.
     */

    final static String SUPPORTED_ROM_FULL_NAME = "Xperia Open Source Project";
    final static String SUPPORTED_ROM_PROP = "ro.xosp.display.version";
    final static String SUPPORTED_ROM_PROP_NAME="XOSP";
}
