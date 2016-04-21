package delta.out386.xosp;

import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;

import com.cjj.MaterialRefreshLayout;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Collection;

public class FindZips {
    Context context;
    boolean isReload=false, isLoading = false;
    final String TAG = Constants.TAG;
    File f = null;
    MaterialRefreshLayout refresh;
    Intent applyDialog = new Intent(Constants.ACTION_APPLY_DIALOG);

    public FindZips(Context context, boolean isReload, MaterialRefreshLayout refresh){
        this.isReload = isReload;
        this.refresh = refresh;
        this.context = context;
    }

    public FlashablesTypeList run() {
        FlashablesTypeList output = null;
        File directory = null;
        boolean directoryExists = true;

        f = new File(context.getFilesDir().toString() + "/FlashablesTypeList");
        if (!isReload && !f.exists()) {
            isLoading = true;
            // Get the fake dialog up
            context.startActivity(new Intent(context, DeltaDialogActivity.class)
                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));

            /* Delay needed as the dialog activity needs time to register
             * the broadcast receiver
             */
            try {
                Thread.sleep(90);
            } catch (InterruptedException e) {
                Log.e(TAG, e.toString());
            }
            applyDialog.putExtra(Constants.DIALOG_MESSAGE, "Loading list of files");
            context.sendBroadcast(applyDialog);
        }

        try {
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
                    directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/thugota");
            }
            catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
            if (directory == null)
                return null;
            if (!directory.exists())
                directoryExists = directory.mkdir();
        if (!directoryExists) {
            Log.e(TAG, "Couldn't create storage directory");
            return null;
        }
        if (!f.exists() || isReload) {
            Collection<File> zipsCollection;
            if (f.exists())
                f.delete();
            zipsCollection = FileUtils.listFiles(directory, new String[]{"zip"}, false);
            output = new FilesCategorize().run(zipsCollection);
            ObjectOutputStream oos;
            if (output != null) {
                try {
                    oos = new ObjectOutputStream(new FileOutputStream(f));
                    oos.writeObject(output);
                    oos.close();
                } catch (Exception e) {
                    Log.e(TAG, e.toString());
                }
                refreshDone();
                return output;
            }
        } else {

            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                output = (FlashablesTypeList) ois.readObject();
                ois.close();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
                refreshDone();
            }
        }
        refreshDone();
        return output;
    }

    public void refreshDone()
    {
        if(refresh == null)
            return;
        try {
            // To display the animation even if the reload happens fast, removes stutters
            Thread.sleep(1000);
        }
        catch(InterruptedException e) {
            Log.e(TAG, e.toString());
        }
        refresh.finishRefresh();
    }
}
