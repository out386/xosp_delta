package delta.out386.borkeddelta;

import android.os.AsyncTask;

/**
 * Created by J-PC on 4/2/2016.
 */
public class Md5TargetCheck extends AsyncTask<Void, Void, Void> {
    String targetPath, targetMd5;
    public Md5TargetCheck(String targetPath, String targetMd5) {
        this.targetPath = targetPath;
        this.targetMd5 = targetMd5;
    }
    @Override
    public Void doInBackground(Void... params) {
        return null;
    }
}
