package delta.out386.borkeddelta;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class DownloadUpdateJson extends AsyncTask<Void, Void, Void> {
    static final int HTTP_READ_TIMEOUT = 30000;
    static final int HTTP_CONNECTION_TIMEOUT = 30000;

    final String URL = Constants.UPDATE_JSON_URL, TAG = Constants.TAG;
    File f;
    View view;

    public DownloadUpdateJson(Context context, View view) {
        f = new File(context.getFilesDir().toString() + "/updateJson");
        this.view = view;
    }
    @Override
    public Void doInBackground(Void... v) {
        HttpsURLConnection urlConnection = null;
        long len = 0;
        if (f.exists())
            f.delete();
        try {
            urlConnection = setupHttpsRequest(URL);
            len = urlConnection.getContentLength();
            InputStream is = urlConnection.getInputStream();
            FileOutputStream os = new FileOutputStream(f, false);
            IOUtils.copy(is, os);
            is.close();
            os.flush();
            os.close();
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        } finally {
            if (urlConnection != null) {
                Log.v(TAG, String.valueOf(len));
                urlConnection.disconnect();
            }
        }
        return null;
    }

    @Override
    public void onPostExecute(Void v){
        new ProcessUpdateJson(view, f).execute();
    }
    private HttpsURLConnection setupHttpsRequest(String urlStr){
        URL url;
        HttpsURLConnection urlConnection;

        try {
            url = new URL(urlStr);
            urlConnection = (HttpsURLConnection) url.openConnection();
            urlConnection.setConnectTimeout(HTTP_CONNECTION_TIMEOUT);
            urlConnection.setReadTimeout(HTTP_READ_TIMEOUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.setDoInput(true);
            urlConnection.connect();
            int code = urlConnection.getResponseCode();
            if (code != HttpsURLConnection.HTTP_OK) {
                Log.e(TAG, String.valueOf(code));
                return null;
            }
            return urlConnection;
        } catch (Exception e) {
            Log.e(TAG, e.toString());
            return null;
        }
    }
}