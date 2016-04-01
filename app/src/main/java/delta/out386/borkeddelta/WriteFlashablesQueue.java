package delta.out386.borkeddelta;

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



/**
 * Created by J-PC on 3/30/2016.
 */
public class WriteFlashablesQueue extends AsyncTask<Void, Void, Void> {
    Flashables flashables;
    Context context;
    public WriteFlashablesQueue(Flashables flashables, Context context) {
        this.flashables = flashables;
        this.context = context;
    }
    @Override
    public Void doInBackground(Void... v) {
        File f = new File(context.getFilesDir().toString() + "/queue");
        FlashablesTypeList flashablesTypeList = null;
        if(f.exists()) {
            try {
                ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f));
                flashablesTypeList = (FlashablesTypeList) ois.readObject();
                ois.close();
            }
            catch(FileNotFoundException e) {
                Log.e("borked", e.toString());
            }
            catch(IOException e) {
                Log.e("borked", e.toString());
            }
            catch(ClassNotFoundException e) {
                Log.e("borked", e.toString());
            }
        }
        else
            flashablesTypeList = new FlashablesTypeList();
        if(flashables != null)
            flashablesTypeList.addFlashable(flashables);
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(f));
            oos.writeObject(flashablesTypeList);
            oos.close();
        } catch (FileNotFoundException e) {
            Log.e("borked", e.toString());
        } catch (IOException e) {
            Log.e("borked", e.toString());
        }
        return null;
    }
}
