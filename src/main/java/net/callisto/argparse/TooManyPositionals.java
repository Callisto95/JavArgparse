package net.callisto.argparse;

public class TooManyPositionals extends RuntimeException {
	public TooManyPositionals(final int size) {
		super(String.format("Too many positional arguments! Only %d expected.", size));
	}
}
