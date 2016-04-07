package delta.out386.borkeddelta;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileInputStream;
import java.util.Scanner;

public class ProcessUpdateJson extends AsyncTask<Void, Void, Void>{
    View view;
    File file;
    TextView tv;
    final String TAG = Constants.TAG;
    String [][] updates;
    // Device will be fetched from rom properties once this is done
    final String DEVICE="sprout4";

    public ProcessUpdateJson(View view, File file){
        this.view = view;
        this.file = file;
        tv = (TextView) view.findViewById(R.id.textView);
    }

    @Override
    public Void doInBackground(Void... params){

        if(!file.exists())
            return null;
        Log.v(TAG, "Parsing update JSON");
        Moshi moshi = new Moshi.Builder().build();
        JsonAdapter<String[][]> jsonAdapter = moshi.adapter(String[][].class);
        String json = "";
        try {
            Scanner sc = new Scanner(new FileInputStream(file));
            while(sc.hasNextLine())
                json = json + "\n" + sc.nextLine();
            Log.v(TAG, json);
            updates = jsonAdapter.fromJson(json);
            Log.v(TAG, String.valueOf(updates.length));
        }
        catch(Exception e) {
            Log.e(TAG, e.toString());
        }
        return null;
    }

    @Override
    public void onPostExecute(Void v){
        tv.setText("PostExec");
        try {
            String temp = "";
            for (String[] a : updates) {
                // As the a[0] will contain device names and as a[i];i!=0 will contain build names
                if (!a[0].equalsIgnoreCase(DEVICE)) {
                    Log.v(TAG, "Not found" + a[0]);
                    continue;
                }
                Log.v(TAG, "found" + a[0]);
                for (String b : a) {
                    Log.v(TAG, b);
                    temp = temp + " " + b;
                }
            }
            tv.setText(temp);
        }
        catch(ArrayIndexOutOfBoundsException e){
            Log.e(TAG, e.toString());
        }
    }

}
