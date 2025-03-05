package net.callisto.argparse.exceptions;

public class ShortNameTooLong extends RuntimeException {
	public ShortNameTooLong(final String shortName) {
		super(String.format("The short name '%s' is too long. Only 1 char long names are supported", shortName));
	}
}
