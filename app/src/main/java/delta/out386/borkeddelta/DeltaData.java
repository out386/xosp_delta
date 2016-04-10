package delta.out386.borkeddelta;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by J-PC on 4/1/2016.
 */
public class DeltaData implements Serializable{
    long targetSize = 0;
    float version = 0.8;
    String sourceMd5 = null;
    String targetMd5 = null;
    String deltaMd5 = null;
    String source;
    String target;
    String delta;

    public DeltaData(String source, String target, String delta)
    {
        this.source = source;
        this.target = target;
        this.delta = delta;
    }
}
