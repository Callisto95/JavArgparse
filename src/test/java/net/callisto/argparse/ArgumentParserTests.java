package net.callisto.argparse;

import net.callisto.argparse.classes.*;
import net.callisto.argparse.classes.invalid.*;
import net.callisto.argparse.exceptions.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTests {
	private static final String PATH = "/dev/null";
	
	@Test
	void testPrimitiveTypes() {
		final String[] args          = { "true", "1", "2", "3", "4", "5", "6" };
		PrimitiveArgs  primitiveArgs = new ArgumentParser<>(PrimitiveArgs.class).parseArgs(args);
		
		assertTrue(primitiveArgs.booleanPrimitive);
		assertEquals(1, primitiveArgs.bytePrimitive);
		assertEquals(2, primitiveArgs.shortPrimitive);
		assertEquals(3, primitiveArgs.intPrimitive);
		assertEquals(4, primitiveArgs.longPrimitive);
		assertEquals(5f, primitiveArgs.floatPrimitive);
		assertEquals(6d, primitiveArgs.doublePrimitive);
	}
	
	@Test
	void testWrapperTypes() {
		final String[] args        = { "true", "1", "2", "3", "4", "5", "6" };
		WrapperArgs    wrapperArgs = new ArgumentParser<>(WrapperArgs.class).parseArgs(args);
		
		assertTrue(wrapperArgs.booleanWrapper);
		// ambiguous methods, casts are necessary :/
		assertEquals(1, (byte) wrapperArgs.byteWrapper);
		assertEquals(2, (short) wrapperArgs.shortWrapper);
		assertEquals(3, wrapperArgs.intWrapper);
		assertEquals(4, wrapperArgs.longWrapper);
		assertEquals(5f, wrapperArgs.floatWrapper);
		assertEquals(6d, wrapperArgs.doubleWrapper);
	}
	
	@Test
	void testDefaultInbuiltTypes() {
		final String[]     args               = { PATH, PATH, PATH };
		DefaultInbuiltArgs defaultInbuiltArgs = new ArgumentParser<>(DefaultInbuiltArgs.class).parseArgs(args);
		
		assertEquals(PATH, defaultInbuiltArgs.aString);
		assertEquals(new File(PATH), defaultInbuiltArgs.aFile);
		assertEquals(Path.of(PATH), defaultInbuiltArgs.aPath);
	}
	
	@Test
	void testNotRegisteredType() {
		final ArgumentParser<ClassWithUnregisteredType> parser = new ArgumentParser<>(ClassWithUnregisteredType.class);
		assertThrows(ImpossibleConversion.class, () -> parser.parseArgs(new String[] { PATH }));
	}
	
	@Test
	void testInvalidClass() {
		assertThrows(IncompatibleClass.class, () -> new ArgumentParser<>(Runnable.class));
		assertThrows(IncompatibleClass.class, () -> new ArgumentParser<>(boolean.class));
		assertThrows(IncompatibleClass.class, () -> new ArgumentParser<>(PrimitiveArgs[].class));
		assertThrows(IncompatibleClass.class, () -> new ArgumentParser<>(Void.class));
	}
	
	@Test
	void testRegisterType() {
		final ArgumentParser<ClassWithUnregisteredType> parser = new ArgumentParser<>(ClassWithUnregisteredType.class);
		
		parser.registerTypeConverter(
			RandomAccessFile.class, s -> {
				try {
					return new RandomAccessFile(s, "r");
				} catch (FileNotFoundException e) {
					throw new RuntimeException(e);
				}
			}
		);
		
		assertDoesNotThrow(() -> parser.parseArgs(new String[] { PATH }));
	}
	
	@Test
	void testArgumentOrderDoesNotMatter() {
		final String[]                                  args   = new String[] { "--true-if-present", PATH, "-s" };
		final ArgumentParser<SimpleArgumentCombination> parser = new ArgumentParser<>(SimpleArgumentCombination.class);
		
		final SimpleArgumentCombination parsedArgs = assertDoesNotThrow(() -> parser.parseArgs(args));
		
		assertTrue(parsedArgs.trueIfPresent);
		assertEquals(PATH, parsedArgs.aString);
		assertTrue(parsedArgs.shortNameFalseIfPresent);
	}
	
	@Test
	void testArgumentTypes() {
		final String[] args = new String[] {
			PATH, "--true-if-present", "--false-if-present", "--count", "--count"
		};
		final ArgumentParser<AllArgumentTypes> parser = new ArgumentParser<>(AllArgumentTypes.class);
		
		final AllArgumentTypes parsedArgs = assertDoesNotThrow(() -> parser.parseArgs(args));
		
		assertEquals(PATH, parsedArgs.defaultType);
		assertTrue(parsedArgs.trueIfPresent);
		assertFalse(parsedArgs.falseIfPresent);
		assertEquals(2, parsedArgs.count);
	}
	
	@Test
	void testInvalidArgumentTypeCombinations() {
		assertThrows(InvalidArgumentCombination.class, () -> new ArgumentParser<>(CountAndPositional.class));
		
		assertThrows(InvalidArgumentCombination.class, () -> new ArgumentParser<>(PositionalAndTrue.class));
		
		assertThrows(InvalidArgumentCombination.class, () -> new ArgumentParser<>(OptionalPositional.class));
	}
	
	@Test
	void testInaccessibleConstructor() {
		class CantTouchThis {
			@Argument
			public String aString;
		}
		
		assertThrows(ConstructorNotAccessible.class, () -> new ArgumentParser<>(CantTouchThis.class));
	}
	
	@Test
	void testCounter() {
		final String                    COUNT       = "--count";
		final String[]                  fullArgs    = { COUNT, COUNT, COUNT, COUNT, COUNT };
		final ArgumentParser<OnlyCount> countParser = new ArgumentParser<>(OnlyCount.class);
		
		for (int i = 0; i <= 5; i++) {
			assertEquals(i, countParser.parseArgs(Arrays.copyOfRange(fullArgs, 0, i)).count);
		}
	}
	
	@Test
	void testOptionalDefaultValues() {
		var parser = new ArgumentParser<>(OptionalDefaultValues.class);
		
		final OptionalDefaultValues values = assertDoesNotThrow(() -> parser.parseArgs(new String[] {}));
		
		assertNull(values.aString);
		assertNull(values.aPath);
		assertEquals(0, values.anInt);
		assertEquals(0, values.aDouble);
	}
}
