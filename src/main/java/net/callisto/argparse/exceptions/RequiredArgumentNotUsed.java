package net.callisto.argparse.exceptions;

public class RequiredArgumentNotUsed extends RuntimeException {
	public RequiredArgumentNotUsed(final String argumentName) {
		super(String.format("The required argument '%s' has not been given", argumentName));
	}
}
