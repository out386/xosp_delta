package delta.out386.borkeddelta;

import android.util.Log;

import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by J-PC on 3/25/2016.
 */
public class FlashablesTypeList implements Serializable {
    List<Flashables> roms=new ArrayList<>();
    List<Flashables> kernels=new ArrayList<>();
    List<Flashables> deltas=new ArrayList<>();
    List<Flashables> others=new ArrayList<>();

    public void addFlashable(Flashables flashables) {
        List<Flashables> search = null;
            if(flashables != null)
                if(flashables.type.equals("rom"))
                    search = roms;
                else if(flashables.type.equals("kernel"))
                    search = kernels;
                else if(flashables.type.equals("delta"))
                    search = deltas;
                else if(flashables.type.equals("other"))
                    search = others;
        if(search == null)
            return;
        for(Flashables current : search)
        {
            if(current.file.equals(flashables.file))
                return;
        }
        search.add(flashables);
    }
}
