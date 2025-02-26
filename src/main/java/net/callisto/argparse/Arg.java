package net.callisto.argparse;

import java.lang.reflect.*;
import java.util.*;

class Arg {
	private final Field        field;
	private final boolean      positional;
	private final boolean      optional;
	private final String       longName;
	private final String       shortName;
	private final ArgumentType type;
	private       boolean      used = false;
	
	Arg(Field field, boolean positional, boolean optional, String longName, String shortName,
		final ArgumentType type) {
		this.field      = field;
		this.positional = positional;
		this.optional   = optional;
		this.longName   = longName;
		this.shortName  = shortName;
		this.type       = type;
	}
	
	public Field field() {
		return field;
	}
	
	public boolean positional() {
		return positional;
	}
	
	public boolean optional() {
		return optional;
	}
	
	public String longName() {
		return longName;
	}
	
	public String shortName() {
		return shortName;
	}
	
	public void use() {
		this.used = true;
	}
	
	public boolean hasBeenUsed() {
		return this.used;
	}
	
	public ArgumentType getArgumentType() {
		return this.type;
	}
	
	@Override
	public boolean equals(final Object object) {
		if (object == null || getClass() != object.getClass()) {
			return false;
		}
		final Arg arg = (Arg) object;
		return positional == arg.positional && optional == arg.optional && used == arg.used && Objects.equals(
			field,
			arg.field
		) && Objects.equals(longName, arg.longName) && Objects.equals(shortName, arg.shortName) && type == arg.type;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(field, positional, optional, longName, shortName, type, used);
	}
}
