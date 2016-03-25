package delta.out386.borkeddelta;

import android.view.View;
import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * Created by J-PC on 3/19/2016.
 */
public class FilesCatagorize {
    Collection<File> fileCollection;
    View rootView;
    List<File> zips;
	Context context;


    public List<Flashables> run(Collection<File> fileCollection, View rootView, Context context) {
        this.fileCollection = fileCollection;
        this.rootView = rootView;
		this.context = context;
        zips=(List<File>) fileCollection;
        //showZips();
        return sortSize();

    }
   /* public void showZips(){
        String output=null;
        TextView outputTV = (TextView) rootView.findViewById(R.id.textView);
        for(File current:zips)
            if(output != null)
                output = output + current.getName() + "\n";
            else
                output = current.getName() + "\n";
        if(output != null)
            outputTV.setText(output);
        else
            outputTV.setText("No zips found");
    }*/

    public List<Flashables> sortSize(){
		if(zips == null)
			return null;
        int zipsize=zips.size();
		SortFileType sortFile = new SortFileType(context);
        long [] sizes = new long [zipsize];
        Flashables [] flashablesArray = new Flashables [zipsize];
		String zipType;
        List<Flashables> output = new ArrayList<>();
        int i=0;
        long size=0;
		
        for(File current:zips){
            size=FileUtils.sizeOf(current);
            if(size < 555745280)
                if((zipType=sortFile.sort(current)) != null)
                	flashablesArray[i++]=new Flashables(current, zipType, size);

        }
        i = 0;
       // TextView outputTV = (TextView) rootView.findViewById(R.id.textView);
        for(Flashables current:flashablesArray)
            if(current != null)
				if(! current.type.equals("noFlash"))
                	    output.add(current);
		return output;
     /*   if(output != null)
            outputTV.setText(output);
        else
            outputTV.setText("No zips found");  */
    }
}
