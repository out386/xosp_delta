package delta.out386.borkeddelta;

import java.io.File;
public class Flashables
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
