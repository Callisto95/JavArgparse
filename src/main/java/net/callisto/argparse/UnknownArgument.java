package net.callisto.argparse;

public class UnknownArgument extends RuntimeException {
	public UnknownArgument(final String argument) {
		super(String.format("Unknown argument: %s", argument));
	}
}
