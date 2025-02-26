package net.callisto.argparse;

import net.callisto.argparse.classes.*;
import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.file.*;

import static org.junit.jupiter.api.Assertions.*;

class ArgumentParserTests {
	private static final String PATH = "/dev/null";
	
	@Test
	void testPrimitives() {
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
	void testWrapper() {
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
}
