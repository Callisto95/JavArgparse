package net.callisto.argparse.classes;

import net.callisto.argparse.*;

import java.io.*;

public class ClassWithUnregisteredType {
	@Argument(positional = true)
	public RandomAccessFile raf;
}
