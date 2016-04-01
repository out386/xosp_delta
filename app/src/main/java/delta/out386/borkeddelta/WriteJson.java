package delta.out386.borkeddelta;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * Created by J-PC on 4/1/2016.
 */
public class WriteJson extends AsyncTask<Void, Void, Void> {
    DeltaData data;
    Context context;
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
            BufferedWriter brData = new BufferedWriter(new FileWriter(context.getFilesDir().toString() + "/currentDelta"));
            brData.write(dataJson);
            brData.close();
        }
        catch(FileNotFoundException e) {
            Log.e("borked", e.toString());
        }
        catch(IOException e) {
            Log.e("borked", e.toString());
        }
        return null;
    }
}