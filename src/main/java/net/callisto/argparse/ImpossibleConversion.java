package net.callisto.argparse;

public class ImpossibleConversion extends RuntimeException {
	public ImpossibleConversion(final String value, final Class<?> target) {
		super(String.format("Cannot convert '%s' into '%s'", value, target));
	}
}
