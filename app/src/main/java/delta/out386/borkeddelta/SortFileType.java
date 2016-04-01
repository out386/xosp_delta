package delta.out386.borkeddelta;

import android.os.AsyncTask;
import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipFile;

import eu.chainfire.libsuperuser.Shell;
public class SortFileType 
{
	File file;
	String assetDir;
	public SortFileType(Context context) {
		assetDir = context.getApplicationContext().getFilesDir().toString();
	}

	public String sort(File file)
	{
		this.file = file;
		boolean other = false;
		Enumeration zipTypeList = null;
		String [] assets = {"dedelta", "zipadjust"};
		for(String asset : assets)
			if(! new File(assetDir + "/" + asset).exists())
				return "noAsset";
		try {
			zipTypeList = new ZipFile(file.toString()).entries();//Shell.SH.run("unzip -l " + file.toString());
		}
		catch(IOException e) {
			Log.e("borked", e.toString());
		}
		
		if(zipTypeList == null)
			return "emptylist";
		while(zipTypeList.hasMoreElements()){
			String line = zipTypeList.nextElement().toString();
			if(line.contains("system.new.dat"))
				return "rom";
			else if(line.contains("zImage"))
				return "kernel";
			else if(line.contains("deltaconfig"))
				return "delta";
			else if(line.contains("update-binary"))
				other = true;
			//else
				//error = error + line + "\n";
		}
		if(other)
			return "other";
		return "noFlash";
	}

}
