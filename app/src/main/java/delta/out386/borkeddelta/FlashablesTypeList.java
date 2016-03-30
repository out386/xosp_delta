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
        if(flashables.type.equals("rom"))
            roms.add(flashables);
        else if(flashables.type.equals("kernel"))
            kernels.add(flashables);
        else if(flashables.type.equals("delta"))
            deltas.add(flashables);
        else if(flashables.type.equals("other"))
            others.add(flashables);
    }
}
