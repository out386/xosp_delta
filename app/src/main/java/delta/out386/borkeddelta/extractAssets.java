package delta.out386.borkeddelta;

import android.os.AsyncTask;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import android.util.Log;
import android.content.Context;
import org.apache.commons.io.IOUtils;
import eu.chainfire.libsuperuser.Shell;

public class extractAssets extends AsyncTask<Void, Void, Void> {
	String storage_dir;
	Context context;

	public extractAssets(String storage, Context context) {
		storage_dir=storage;
		this.context=context;
	}
	@Override
	protected Void doInBackground(Void... p1) {
		String [] assets= {"dedelta","zipadjust"};
		for( String asset : assets) {
        	InputStream is = null;
        	OutputStream os = null;
        	if(storage_dir == null)
				return null;
        	File assetFile = new File(storage_dir + "/" + asset);
        	if (assetFile.exists())
            	assetFile.setWritable(true, true);
        	else {
				try {
					is = context.getAssets().open(asset);
					os = new FileOutputStream(assetFile);
					IOUtils.copy(is, os);
					is.close();
					os.flush();
					os.close();
				}
				catch (IOException e) {
					Log.e("borked", e.toString());
				}
        	}
        	Shell.SH.run("chmod 777 " + storage_dir + "/" + asset);
		}
		return null;
	}
}
