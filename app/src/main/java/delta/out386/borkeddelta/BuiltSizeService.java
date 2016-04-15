package delta.out386.borkeddelta;

import android.app.IntentService;
import android.content.Intent;
import android.util.Log;

import java.io.File;

/**
 * Created by J-PC on 4/15/2016.
 */
public class BuiltSizeService extends IntentService {
    final String TAG = Constants.TAG;
    public BuiltSizeService(){
        super("SizeService");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.v(TAG, "Second service");
        String target = intent.getStringExtra("target");
        long targetSize = intent.getLongExtra("targetSize", 0), currentSize = 0, lastSize = 0;
        final int INTERVAL = 10;
        boolean isFileReady = false;
        Intent progress = new Intent(Constants.PROGRESS_DIALOG);
        while(!isFileReady || currentSize < targetSize) {
            File targetFile = new File(target);
            if(targetFile.exists()) {
                isFileReady = true;
                currentSize = targetFile.length();
                float progressValue = (float)currentSize/targetSize * 100;
                long speed = (currentSize - lastSize) / INTERVAL;
                //Log.v(TAG, String.valueOf(speed));
                progress.putExtra(Constants.PROGRESS, progressValue);
                progress.putExtra(Constants.SPEED, speed);
                sendBroadcast(progress);
                try {
                    Thread.sleep(INTERVAL * 1000);
                }
                catch (InterruptedException e) {
                    Log.e(TAG, e.toString());
                }
                lastSize = currentSize;
            }
        }
    }
}
