package net.callisto.argparse.exceptions;

public class ConstructorNotAccessible extends RuntimeException {
	public <T> ConstructorNotAccessible(final Class<T> targetClass) {
		super(String.format("The no-arg constructor of the class '%s' is not accessible", targetClass.getName()));
	}
}
