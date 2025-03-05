package net.callisto.argparse.exceptions;

import net.callisto.argparse.*;

public class ClassMismatchException extends RuntimeException {
	public ClassMismatchException(final Argument argumentAnnotation, final Class<?> type) {
		super(String.format(
			"Invalid class combination! Got '%s', but only %s %s allowed!",
			type,
			argumentAnnotation.type().getAllowedClasses(),
			argumentAnnotation.type().getAllowedClasses().size() == 1 ? "is" : "are"
		));
	}
}
