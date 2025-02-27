package net.callisto.argparse.classes.invalid;

import net.callisto.argparse.*;

public class CountAndPositional {
	@Argument(positional = true, type = ArgumentType.COUNT)
	public String aString;
}
