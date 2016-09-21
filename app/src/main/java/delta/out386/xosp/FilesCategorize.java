package delta.out386.xosp;
/*
 * Copyright (C) 2016 Ritayan Chakraborty (out386)
 */
/*
 * This file is part of XOSPDelta.
 *
 * XOSPDelta is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * XOSPDelta is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with XOSPDelta. If not, see <http://www.gnu.org/licenses/>.
 */

import android.content.Context;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import android.util.Log;
import org.apache.commons.io.FileUtils;

import static delta.out386.xosp.Constants.TAG;

public class FilesCategorize {
    List<File> zips;

    public FlashablesTypeList run(Collection<File> fileCollection) {
        zips=(List<File>) fileCollection;
        return sortSize();

    }

    public FlashablesTypeList sortSize(){
		if(zips == null)
			return null;
		SortFileType sortFile = new SortFileType();
        FlashablesTypeList flashablesTypeList = new FlashablesTypeList();
		String zipType = null;
        long size=0;
		
        for(File current:zips){
            size=FileUtils.sizeOf(current);
            try {
                zipType=sortFile.sort(current);
            } catch(IOException e) {
                Log.i(TAG, "sortSize:  " + "File is not a zip");
            }
            if(zipType != null)
                flashablesTypeList.addFlashable(new Flashables(current, zipType, size));
        }
        return flashablesTypeList;
    }
}
