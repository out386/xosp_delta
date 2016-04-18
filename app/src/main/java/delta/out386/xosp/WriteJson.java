package delta.out386.xosp;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by J-PC on 4/1/2016.
 */
public class WriteJson extends AsyncTask<Void, Void, Void> {
    DeltaData data;
    Context context;
    final String TAG = Constants.TAG;
    public WriteJson(DeltaData data, Context context) {
        this.context = context;
        this.data = data;
    }
    @Override
    public Void doInBackground(Void... params) {
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<DeltaData> jsonAdapter = moshi.adapter(DeltaData.class);
        String dataJson = jsonAdapter.toJson(data);
        try {
            File file = new File(context.getFilesDir().toString() + "/currentDelta");
            if(file.exists())
                file.delete();
            BufferedWriter brData = new BufferedWriter(new FileWriter(file));
            brData.write(dataJson);
            brData.close();
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }
}