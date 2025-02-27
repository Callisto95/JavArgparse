package net.callisto.argparse.classes.invalid;

import net.callisto.argparse.*;

public class PositionalAndTrue {
	@Argument(positional = true, type = ArgumentType.TRUE_IF_PRESENT)
	boolean trueIfPresent;
}
