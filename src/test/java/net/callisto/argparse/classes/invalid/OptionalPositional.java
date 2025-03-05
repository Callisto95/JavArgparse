package net.callisto.argparse.classes.invalid;

import net.callisto.argparse.*;

public class OptionalPositional {
	@Argument(positional = true, optional = true)
	public String positionalOptional;
}
