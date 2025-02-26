package net.callisto.argparse.classes;

import net.callisto.argparse.*;

public class WrapperArgs {
	@Argument(positional = true)
	public Boolean booleanWrapper;
	@Argument(positional = true)
	public Byte    byteWrapper;
	@Argument(positional = true)
	public Short   shortWrapper;
	@Argument(positional = true)
	public Integer intWrapper;
	@Argument(positional = true)
	public Long    longWrapper;
	@Argument(positional = true)
	public Float   floatWrapper;
	@Argument(positional = true)
	public Double  doubleWrapper;
}
