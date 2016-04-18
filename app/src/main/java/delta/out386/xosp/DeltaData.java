package delta.out386.xosp;

import java.io.Serializable;

/**
 * Created by J-PC on 4/1/2016.
 */
public class DeltaData implements Serializable{
    long targetSize = 0;
    float version = 2.1f;
    String sourceMd5 = null;
    String sourceDecMd5 = null;
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
