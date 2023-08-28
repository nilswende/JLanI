package com.wn.nlp.jlani.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class LanguageTest {
	@Test
	void testNull() {
		assertThrows(NullPointerException.class, () -> new Language(null));
	}
	@Test
	void testEmpty() {
		assertThrows(IllegalArgumentException.class, () -> new Language(""));
	}
	@Test
	void testBlank() {
		assertThrows(IllegalArgumentException.class, () -> new Language(" "));
	}
	@Test
	void testOk() {
		var name = "lang";
		assertEquals(name, new Language(name).name());
	}
	@Test
	void testLowercase() {
		var name = "LANG";
		assertEquals(name.toLowerCase(), new Language(name).name());
	}
}