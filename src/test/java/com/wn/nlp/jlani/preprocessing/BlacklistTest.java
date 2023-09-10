package com.wn.nlp.jlani.preprocessing;

import com.wn.nlp.jlani.preprocessing.impl.SimpleBlacklist;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BlacklistTest {
	@ParameterizedTest
	@MethodSource
	void test(final String  blacklistStr, final String input, final String expected) {
		var blacklist = SimpleBlacklist.ofReader(new StringReader(blacklistStr));
		var actual = Arrays.stream(input.split(" "))
				.filter(s -> !blacklist.contains(new Word(s)))
				.collect(Collectors.joining(" "));
		assertEquals(expected, actual);
	}
	
	static Stream<Arguments> test() {
		return Stream.of(//
				arguments("", "abc abc", "abc abc"),
				arguments("""
							bcd
							cde""", "abc abc", "abc abc"),
				arguments("""
							bcd
							cde""", "abc bcd abc", "abc abc"),
				arguments("""
							bcd
							cde""", "abcd abc", "abcd abc")
		);
	}
	
	@Test
	void testMissingProperty() {
		assertNotNull(SimpleBlacklist.ofPath(null));
	}
	
	@Test
	void testBlankProperty() {
		assertNotNull(SimpleBlacklist.ofPath(" "));
	}
	
	@Test
	void testMissingFile() {
		assertNotNull(SimpleBlacklist.ofPath("./missing.txt"));
	}
	
	@Test
	void testFile() {
		var actual = SimpleBlacklist.ofPath("./src/test/resources/blacklist.txt");
		assertNotNull(actual);
		assertTrue(actual.contains(new Word("list")));
	}
}