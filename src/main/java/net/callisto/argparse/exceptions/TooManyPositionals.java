package net.callisto.argparse.exceptions;

public class TooManyPositionals extends RuntimeException {
	public TooManyPositionals() {
		super("Too many positional arguments given!");
	}
}
