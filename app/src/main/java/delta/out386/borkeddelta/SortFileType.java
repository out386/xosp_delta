package delta.out386.borkeddelta;

import android.os.AsyncTask;
import android.content.Context;
import java.io.File;
import java.util.List;
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
		List <String> zipTypeList;
		String [] assets = {"dedelta", "zipadjust"};
		for(String asset : assets)
			if(! new File(assetDir + "/" + asset).exists())
				return "noAsset";
		
		zipTypeList = Shell.SH.run("unzip -l " + file.toString());
		
		if(zipTypeList == null)
			return "emptylist";
		for(String line : zipTypeList) {
			if(line.contains("system.new.dat"))
				return "rom";
			else if(line.contains("zImage"))
				return "kernel";
			else if(line.contains("delta"))
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
