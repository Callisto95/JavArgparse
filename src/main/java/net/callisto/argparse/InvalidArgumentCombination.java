package net.callisto.argparse;

import java.lang.reflect.*;

public class InvalidArgumentCombination extends RuntimeException {
	public InvalidArgumentCombination(final Field field) {
		super(String.format("The argument on the field '%s' has an invalid combination of types", field.getName()));
	}
}
