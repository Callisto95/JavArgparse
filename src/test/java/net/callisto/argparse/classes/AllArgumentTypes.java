package net.callisto.argparse.classes;

import net.callisto.argparse.*;

public class AllArgumentTypes {
	@Argument(type = ArgumentType.DEFAULT, positional = true)
	public String  defaultType;
	@Argument(type = ArgumentType.TRUE_IF_PRESENT)
	public boolean trueIfPresent;
	@Argument(type = ArgumentType.FALSE_IF_PRESENT)
	public boolean falseIfPresent;
	@Argument(type = ArgumentType.COUNT)
	public int     count;
}
