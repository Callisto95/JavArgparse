package net.callisto.argparse;

import java.util.*;

public enum ArgumentType {
	DEFAULT,
	COUNT(
		Byte.class,
		Short.class,
		Integer.class,
		Float.class,
		Double.class,
		byte.class,
		short.class,
		int.class,
		float.class,
		double.class
	),
	TRUE_IF_PRESENT(Boolean.class, boolean.class),
	FALSE_IF_PRESENT(Boolean.class, boolean.class);
	
	private final List<Class<?>> allowedClasses = new ArrayList<>();
	
	ArgumentType(Class<?>... allowedClasses) {
		this.allowedClasses.addAll(List.of(allowedClasses));
	}
	
	public boolean classIsAllowed(Class<?> clazz) {
		if (this.allowedClasses.isEmpty()) {
			return true;
		}
		
		return this.allowedClasses.contains(clazz);
	}
	
	public List<Class<?>> getAllowedClasses() {
		return this.allowedClasses;
	}
}
