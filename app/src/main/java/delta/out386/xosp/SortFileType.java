package delta.out386.xosp;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.zip.ZipFile;

public class SortFileType
{
	final String TAG = Constants.TAG;
	File file;

	public String sort(File file)
	{
		this.file = file;
		boolean other = false;
		Enumeration zipTypeList = null;
		try {
			zipTypeList = new ZipFile(file.toString()).entries();
		}
		catch(IOException e) {
			Log.e(TAG, e.toString());
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
		}
		if(other)
			return "other";
		return "noFlash";
	}

}
