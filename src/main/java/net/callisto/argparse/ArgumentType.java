package net.callisto.argparse;

import java.util.*;

public enum ArgumentType {
	DEFAULT,
	COUNT(Short.class, Integer.class, Float.class, Double.class),
	TRUE_IF_PRESENT(Boolean.class, boolean.class),
	FALSE_IF_PRESENT(Boolean.class, boolean.class);
	
	private final List<Class<?>> allowedClasses = new ArrayList<>();
	
	private ArgumentType(Class<?>... allowedClasses) {
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
