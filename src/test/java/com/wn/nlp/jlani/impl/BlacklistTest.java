package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class BlacklistTest {
	@ParameterizedTest
	@MethodSource
	void test(final String  blacklistStr, final String input, final String expected) {
		var blacklist = InMemoryBlacklist.ofReader(new StringReader(blacklistStr));
		var actual = Arrays.stream(input.split(" "))
				.filter(s -> !blacklist.isBlacklisted(new Word(s)))
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
		var properties = new Properties();
		assertNotNull(InMemoryBlacklist.ofProperty(properties));
	}
	
	@Test
	void testBlankProperty() {
		var properties = new Properties();
		properties.setProperty(InMemoryBlacklist.PROPERTY, " ");
		assertNotNull(InMemoryBlacklist.ofProperty(properties));
	}
	
	@Test
	void testMissingFile() {
		var properties = new Properties();
		properties.setProperty(InMemoryBlacklist.PROPERTY, "./missing.txt");
		assertNotNull(InMemoryBlacklist.ofProperty(properties));
	}
	
	@Test
	void testFile() {
		var properties = new Properties();
		properties.setProperty(InMemoryBlacklist.PROPERTY, "./src/test/resources/blacklist.txt");
		var actual = InMemoryBlacklist.ofProperty(properties);
		assertNotNull(actual);
		assertTrue(actual.isBlacklisted(new Word("list")));
	}
}