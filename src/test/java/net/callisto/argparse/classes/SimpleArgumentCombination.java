package net.callisto.argparse.classes;

import net.callisto.argparse.*;

public class SimpleArgumentCombination {
	@Argument(positional = true)
	public String aString;
	@Argument(type = ArgumentType.TRUE_IF_PRESENT)
	public boolean trueIfPresent;
	@Argument(shortName = "s", type = ArgumentType.TRUE_IF_PRESENT)
	public boolean shortNameFalseIfPresent;
}
