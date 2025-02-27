package net.callisto.argparse;

import net.callisto.argparse.classes.*;
import net.callisto.argparse.classes.invalid.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTests {
	private static final String   PATH         = "/dev/null";
	private static final String[] NO_ARGUMENTS = new String[] {};
	
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
		final var countAndPositionalArgumentParser = new ArgumentParser<>(CountAndPositional.class);
		assertThrows(InvalidArgumentCombination.class, () -> countAndPositionalArgumentParser.parseArgs(NO_ARGUMENTS));
		
		final var positionalAndTrueArgumentParser = new ArgumentParser<>(PositionalAndTrue.class);
		assertThrows(InvalidArgumentCombination.class, () -> positionalAndTrueArgumentParser.parseArgs(NO_ARGUMENTS));
	}
	
	@Test
	void testInaccessibleConstructor() {
		class CantTouchThis {
			@Argument
			public String aString;
		}
		
		final ArgumentParser<CantTouchThis> parser = new ArgumentParser<>(CantTouchThis.class);
		assertThrows(ConstructorNotAccessible.class, () -> parser.parseArgs(NO_ARGUMENTS));
	}
}
