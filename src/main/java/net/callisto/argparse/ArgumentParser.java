package net.callisto.argparse;

import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.util.*;
import java.util.function.*;

public class ArgumentParser<T> {
	private static final String LONG_ARGUMENT_PREFIX = "--";
	
	final Class<T> targetClass;
	
	private final List<Arg>         relativeArguments   = new ArrayList<>();
	private final List<Arg>         positionalArguments = new ArrayList<>();
	private final Map<Arg, Integer> countedArgs         = new HashMap<>();
	private       int               positionalIndex     = 0;
	private       T                 object;
	
	private final Map<Class<?>, Function<String, ?>> conversionFunctions = new HashMap<>();
	
	public ArgumentParser(final Class<T> targetClass) {
		if (targetClass.isInterface() || targetClass.isPrimitive() || targetClass.isArray() || Void.class.equals(
			targetClass)) {
			throw new IncompatibleClass(targetClass);
		}
		
		this.targetClass = targetClass;
		this.initializeDefaultConversionFunctions();
	}
	
	private void initializeDefaultConversionFunctions() {
		this.registerTypeConverter(String.class, s -> s);
		this.registerTypeConverter(Boolean.class, Boolean::parseBoolean);
		this.registerTypeConverter(boolean.class, Boolean::parseBoolean);
		this.registerTypeConverter(Byte.class, Byte::parseByte);
		this.registerTypeConverter(byte.class, Byte::parseByte);
		this.registerTypeConverter(Short.class, Short::parseShort);
		this.registerTypeConverter(short.class, Short::parseShort);
		this.registerTypeConverter(Integer.class, Integer::parseInt);
		this.registerTypeConverter(int.class, Integer::parseInt);
		this.registerTypeConverter(Long.class, Long::parseLong);
		this.registerTypeConverter(long.class, Long::parseLong);
		this.registerTypeConverter(Float.class, Float::parseFloat);
		this.registerTypeConverter(float.class, Float::parseFloat);
		this.registerTypeConverter(Double.class, Double::parseDouble);
		this.registerTypeConverter(double.class, Double::parseDouble);
		this.registerTypeConverter(Path.class, Path::of);
		this.registerTypeConverter(File.class, File::new);
	}
	
	public <X> void registerTypeConverter(final Class<X> target, final Function<String, X> function) {
		this.conversionFunctions.put(target, function);
	}
	
	public T parseArgs(final String[] arguments) {
		this.object = instanciateObject();
		
		registerArguments();
		
		handleArguments(arguments);
		
		verifyArguments();
		
		return this.object;
	}
	
	private T instanciateObject() {
		try {
			final Constructor<T> constructor = this.targetClass.getDeclaredConstructor();
			final boolean        access      = constructor.canAccess(null);
			constructor.setAccessible(true);
			final T instance = constructor.newInstance();
			constructor.setAccessible(access);
			
			return instance;
		} catch (final InstantiationException |
		               IllegalAccessException |
		               InvocationTargetException |
		               NoSuchMethodException exc) {
			throw new RuntimeException(exc); // NOSONAR TODO: do not use RuntimeException
		}
	}
	
	private void handleArguments(final String[] arguments) {
		int i = 0;
		while (i < arguments.length) {
			final String argument = arguments[i];
			
			int usedArguments;
			
			if (argument.charAt(0) != '-') {
				usedArguments = handlePositional(argument);
			} else if (LONG_ARGUMENT_PREFIX.equals(argument.substring(0, 2))) {
				usedArguments = handleLongArgument(arguments, i);
			} else {
				usedArguments = handleShortArgument(arguments, i);
			}
			
			i += usedArguments;
		}
	}
	
	private void verifyArguments() {
		if (this.positionalIndex != this.positionalArguments.size()) {
			throw new NotEnoughArguments();
		}
		
		for (Arg argument : this.relativeArguments) {
			if (!argument.hasBeenUsed()) {
				if (!argument.optional()) {
					throw new RequiredArgumentNotUsed(argument.longName());
				}
				
				switch (argument.getArgumentType()) {
					case TRUE_IF_PRESENT -> setField(argument.field(), this.object, "false");
					case FALSE_IF_PRESENT -> setField(argument.field(), this.object, "true");
					case COUNT -> setField(
						argument.field(),
						this.object,
						String.valueOf(this.countedArgs.get(argument))
					);
					default -> { /* field is already set */ }
				}
			}
		}
	}
	
	private void registerArguments() {
		final List<Field> argumentFields = Arrays.stream(this.targetClass.getDeclaredFields())
			.filter(field -> field.isAnnotationPresent(Argument.class))
			.toList();
		
		for (Field field : argumentFields) {
			// only get the first @Argument, all other can be ignored
			final Argument argumentAnnotation = field.getAnnotationsByType(Argument.class)[0];
			
			if (!argumentAnnotation.type().classIsAllowed(field.getType())) {
				throw new ClassMismatchException(argumentAnnotation, field.getType());
			}
			
			final String kebabName = NameConverter.camelToKebabCase(field.getName());
			
			String shortName = argumentAnnotation.shortName();
			if ("".equals(shortName)) {
				shortName = null;
			}
			
			final Arg argument = new Arg(
				field,
				argumentAnnotation.positional(),
				argumentAnnotation.optional(),
				kebabName,
				shortName,
				argumentAnnotation.type()
			);
			
			registerArgument(argument);
		}
	}
	
	private void registerArgument(final Arg argument) {
		if (this.relativeArguments.stream().anyMatch(arg -> Objects.equals(arg.longName(), argument.longName()))) {
			throw new DuplicateArgument(argument.longName());
		}
		if (argument.shortName() != null && this.relativeArguments.stream()
			.anyMatch(arg -> Objects.equals(arg.shortName(), argument.shortName()))) {
			throw new DuplicateArgument(argument.shortName());
		}
		
		if (argument.positional()) {
			this.positionalArguments.add(argument);
		} else {
			this.relativeArguments.add(argument);
		}
		
		if (argument.getArgumentType() == ArgumentType.COUNT) {
			this.countedArgs.put(argument, 0);
		}
	}
	
	private int handleShortArgument(final String[] arguments, final int i) {
		final String name = arguments[i].substring(1);
		
		// e.g. -xzf
		if (name.length() > 1) {
			return handleCombinedShortArguments(arguments, i);
		}
		
		final Optional<Arg> argOptional = this.relativeArguments.stream()
			.filter(arg -> Objects.equals(arg.shortName(), name))
			.findFirst();
		
		if (argOptional.isEmpty()) {
			throw new IllegalStateException();
		}
		
		final Arg arg = argOptional.get();
		
		if (arg.hasBeenUsed()) {
			throw new DuplicateArgument(arg.longName());
		}
		
		if (arg.getArgumentType() == ArgumentType.COUNT) {
			this.countedArgs.merge(arg, 1, Integer::sum);
			return 1;
		}
		
		int usedArgs = 1;
		final String value = switch (arg.getArgumentType()) {
			case TRUE_IF_PRESENT -> "true";
			case FALSE_IF_PRESENT -> "false";
			default -> {
				if (i + 1 == arguments.length) {
					throw new NotEnoughArguments();
				}
				usedArgs = 2;
				yield arguments[i + 1];
			}
		};
		
		arg.use();
		setField(arg.field(), this.object, value);
		return usedArgs;
	}
	
	private int handleCombinedShortArguments(final String[] arguments, final int i) {
		final String argumentCombination = arguments[i].substring(1);
		int          maxUsedArguments    = 0;
		
		for (String s : argumentCombination.split("")) {
			// instead of xzf treat it as x, then as z, then as f
			final String[] argumentCopy = Arrays.copyOf(arguments, arguments.length);
			argumentCopy[i] = "-" + s;
			final int usedArgs = this.handleShortArgument(argumentCopy, i);
			
			// two arguments used the same following arg
			if (usedArgs == 2 && maxUsedArguments == 2) {
				throw new InvalidCombination();
			}
			
			if (maxUsedArguments < usedArgs) {
				maxUsedArguments = usedArgs;
			}
		}
		
		return maxUsedArguments;
	}
	
	private int handleLongArgument(final String[] arguments, final int i) {
		if (i + 1 == arguments.length) {
			throw new NotEnoughArguments();
		}
		
		final String name      = arguments[i].substring(2);
		final String camelName = NameConverter.kebabToCamel(name);
		
		final Optional<Arg> argOptional = this.relativeArguments.stream()
			.filter(arg -> Objects.equals(arg.longName(), camelName))
			.findFirst();
		
		if (argOptional.isEmpty()) {
			throw new IllegalStateException();
		}
		
		final Arg arg = argOptional.get();
		
		if (arg.hasBeenUsed()) {
			throw new DuplicateArgument(arg.longName());
		}
		
		if (arg.getArgumentType() == ArgumentType.COUNT) {
			this.countedArgs.merge(arg, 1, Integer::sum);
			return 1;
		}
		
		int usedArgs = 1;
		final String value = switch (arg.getArgumentType()) {
			case TRUE_IF_PRESENT -> "true";
			case FALSE_IF_PRESENT -> "false";
			default -> {
				if (i + 1 == arguments.length) {
					throw new NotEnoughArguments();
				}
				usedArgs = 2;
				yield arguments[i + 1];
			}
		};
		
		arg.use();
		setField(arg.field(), this.object, value);
		return usedArgs;
	}
	
	private int handlePositional(final String argument) {
		if (this.positionalIndex == this.positionalArguments.size()) {
			throw new TooManyPositionals(this.positionalArguments.size());
		}
		
		final Arg arg = this.positionalArguments.get(this.positionalIndex);
		
		setField(arg.field(), this.object, argument);
		
		this.positionalIndex++;
		return 1;
	}
	
	private void setField(final Field field, final Object object, final String value) {
		try {
			// ! TODO: type cast!
			final boolean access = field.canAccess(object);
			field.setAccessible(true); // NOSONAR
			field.set(object, this.convertType(value, field.getType())); // NOSONAR
			field.setAccessible(access);
		} catch (IllegalAccessException exc) {
			throw new IllegalStateException(exc);
		}
	}
	
	private Object convertType(final String value, final Class<?> target) {
		final Function<String, ?> conversionFunction = this.conversionFunctions.get(target);
		
		if (conversionFunction == null) {
			throw new ImpossibleConversion(value, target);
		}
		
		return conversionFunction.apply(value);
	}
}
