package delta.out386.borkeddelta;

import java.io.File;
import java.io.Serializable;

public class Flashables implements Serializable
{
	File file;
	String type;
    long size;
	public Flashables(File file, String type, long size) {
		this.file = file;
		this.type = type;
		this.size=size;
	}
}
