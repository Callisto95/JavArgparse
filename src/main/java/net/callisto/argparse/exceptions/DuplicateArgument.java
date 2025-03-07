package net.callisto.argparse.exceptions;

public class DuplicateArgument extends RuntimeException {
	public DuplicateArgument(final String entry) {
		super(String.format("more than one instance of argument %s!", entry));
	}
}
