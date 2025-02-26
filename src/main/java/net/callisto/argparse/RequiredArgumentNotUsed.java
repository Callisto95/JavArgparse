package net.callisto.argparse;

public class RequiredArgumentNotUsed extends RuntimeException {
	public RequiredArgumentNotUsed(final String argumentName) {
		super(String.format("The required argument '%s' has not been used", argumentName));
	}
}
