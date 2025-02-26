package net.callisto.argparse.classes;

import net.callisto.argparse.*;

import java.io.*;
import java.nio.file.*;

public class DefaultInbuiltArgs {
	@Argument(positional = true)
	public String aString;
	@Argument(positional = true)
	public File aFile;
	@Argument(positional = true)
	public Path aPath;
}
