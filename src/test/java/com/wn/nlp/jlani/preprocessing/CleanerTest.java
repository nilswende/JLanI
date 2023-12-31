package com.wn.nlp.jlani.preprocessing;

import com.wn.nlp.jlani.preprocessing.impl.RegexCleaner;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class CleanerTest {
	@ParameterizedTest
	@MethodSource
	void test(final String input, final String expected) {
		final var actual = RegexCleaner.ofRegex("[,\\?]").apply(input);
		assertEquals(expected, actual);
	}
	
	static Stream<Arguments> test() {
		return Stream.of(//
				arguments("abc abc", "abc abc"),
				arguments("abc11, abc", "abc11 abc"),
				arguments("abc?abc", "abcabc"),
				arguments("abcßabc", "abcßabc")
		);
	}
	
	@Test
	void testEmpty() {
		var string = "in, put";
		final var actual = RegexCleaner.ofRegex("").apply(string);
		assertEquals(string, actual);
	}
}