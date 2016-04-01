package delta.out386.borkeddelta;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.Serializable;

/**
 * Created by J-PC on 4/1/2016.
 */
public class DeltaData implements Serializable{
    long targetSize = 0;
    String sourceMd5 = null;
    String targetMd5 = null;
    String deltaMd5 = null;
    String source;
    String target;
    String delta;
    /*public DeltaData(String sourceMd5, String targetMd5, String deltaMd5, File source, File target, File delta)
    {
        this.targetSize = FileUtils.sizeOf(target);
        this.sourceMd5 = sourceMd5;
        this.targetMd5 = targetMd5;
        this.deltaMd5 = deltaMd5;
        this.source = source;
        this.target = target;
    }*/
    public DeltaData(String source, String target, String delta)
    {
        this.source = source;
        this.target = target;
        this.delta = delta;
    }
}
