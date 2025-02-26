package net.callisto.argparse;

public class NotEnoughArguments extends RuntimeException {
	public NotEnoughArguments() {
		super("Not enough arguments provided!");
	}
}
