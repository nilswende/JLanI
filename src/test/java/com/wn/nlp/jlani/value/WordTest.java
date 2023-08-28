package com.wn.nlp.jlani.value;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordTest {
	@Test
	void testNull() {
		assertThrows(NullPointerException.class, () -> new Word(null));
	}
	@Test
	void testOk() {
		var value = "ok";
		assertEquals(value, new Word(value).value());
	}
}