package net.callisto.argparse.classes;

import net.callisto.argparse.*;

import java.nio.file.*;

public class OptionalDefaultValues {
	@Argument(optional = true)
	public String aString;
	@Argument(optional = true)
	public int anInt;
	@Argument(optional = true)
	public double aDouble;
	@Argument(optional = true)
	public Path aPath;
}
