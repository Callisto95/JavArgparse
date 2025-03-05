package net.callisto.argparse.exceptions;

public class NotEnoughArguments extends RuntimeException {
	public NotEnoughArguments() {
		super("Not enough arguments provided!");
	}
}
