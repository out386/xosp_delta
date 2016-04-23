package delta.out386.xosp;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;

public class BuiltSizeService extends IntentService {
    final String TAG = Constants.TAG;
    public BuiltSizeService(){
        super("SizeService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        String target = intent.getStringExtra("target");
        long targetSize = intent.getLongExtra("targetSize", 0), currentSize = 0;
        final int INTERVAL = 10;
        boolean isFileReady = false;
        Intent progress = new Intent(Constants.PROGRESS_DIALOG);
        while(!isFileReady || currentSize < targetSize) {
            File targetFile = new File(target);
            if(targetFile.exists()) {
                isFileReady = true;
                currentSize = targetFile.length();
                int progressValue = (int) ((float)currentSize/targetSize * 100);
                progress.putExtra(Constants.PROGRESS, progressValue);
                sendBroadcast(progress);
                try {
                    Thread.sleep(INTERVAL * 1000);
                }
                catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }
            }
        }
    }
}
