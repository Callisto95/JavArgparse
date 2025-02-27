package net.callisto.argparse;

import java.lang.annotation.*;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface Argument {
	String shortName() default "";
	
	boolean optional() default false;
	
	boolean positional() default false;
	
	ArgumentType type() default ArgumentType.DEFAULT;
}
