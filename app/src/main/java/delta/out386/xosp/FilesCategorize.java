package delta.out386.xosp;

import android.content.Context;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Created by J-PC on 3/19/2016.
 */
public class FilesCategorize {
    List<File> zips;
	Context context;


    public FlashablesTypeList run(Collection<File> fileCollection, Context context) {
		this.context = context;
        zips=(List<File>) fileCollection;
        return sortSize();

    }

    public FlashablesTypeList sortSize(){
		if(zips == null)
			return null;
		SortFileType sortFile = new SortFileType(context);
        FlashablesTypeList flashablesTypeList = new FlashablesTypeList();
		String zipType;
        long size=0;
		
        for(File current:zips){
            size=FileUtils.sizeOf(current);
            if((zipType=sortFile.sort(current)) != null)
                flashablesTypeList.addFlashable(new Flashables(current, zipType, size));
        }
        return flashablesTypeList;//.roms;
    }
}
