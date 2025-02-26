package net.callisto.argparse.classes;

import net.callisto.argparse.*;

public class PrimitiveArgs {
	@Argument(positional = true)
	public boolean booleanPrimitive;
	@Argument(positional = true)
	public byte    bytePrimitive;
	@Argument(positional = true)
	public short   shortPrimitive;
	@Argument(positional = true)
	public int     intPrimitive;
	@Argument(positional = true)
	public long    longPrimitive;
	@Argument(positional = true)
	public float   floatPrimitive;
	@Argument(positional = true)
	public double  doublePrimitive;
}
