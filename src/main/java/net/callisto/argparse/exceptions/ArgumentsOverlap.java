package net.callisto.argparse.exceptions;

public class ArgumentsOverlap extends RuntimeException {
	public ArgumentsOverlap() {
		super("Invalid combination of arguments: multiple arguments requested the same argument");
	}
}
