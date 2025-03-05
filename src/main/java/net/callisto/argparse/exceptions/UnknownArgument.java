package net.callisto.argparse.exceptions;

public class UnknownArgument extends RuntimeException {
	public UnknownArgument(final String argument) {
		super(String.format("Unknown argument: %s", argument));
	}
}
