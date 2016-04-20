package delta.out386.xosp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class WriteFlashablesQueue extends AsyncTask<Void, Void, Void> {
    Flashables flashables;
    Context context;
    final String TAG = Constants.TAG;
    boolean isReload = false;

    public WriteFlashablesQueue(Flashables flashables, Context context, boolean isReload) {
        this.flashables = flashables;
        this.context = context;
        this.isReload = isReload;
    }
    @Override
    public Void doInBackground(Void... v) {
        File f = new File(context.getFilesDir().toString() + "/queue");
        FlashablesTypeList flashablesTypeList = null;
        if(isReload && f.exists())
            f.delete();
        if(f.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                flashablesTypeList = (FlashablesTypeList) ois.readObject();
                ois.close();
            }
            catch(Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        else
            flashablesTypeList = new FlashablesTypeList();
        if(flashables != null && flashablesTypeList != null)
            flashablesTypeList.addFlashable(flashables);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(flashablesTypeList);
            oos.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }
}
