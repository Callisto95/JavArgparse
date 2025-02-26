package net.callisto.argparse;

public class IncompatibleClass extends RuntimeException {
	public <T> IncompatibleClass(final Class<T> targetClass) {
		super(String.format("The given class '%s' cannot be used to parse arguments", targetClass));
	}
}
