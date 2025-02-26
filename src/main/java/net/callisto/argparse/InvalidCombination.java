package net.callisto.argparse;

public class InvalidCombination extends RuntimeException {
	public InvalidCombination() {
		super("Invalid combination of arguments: multiple arguments requested the same argument");
	}
}
