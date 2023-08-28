package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {
	private InMemoryWordList getWordList(final String wordlist) {
		return InMemoryWordList.ofSerializedFileReader(new StringReader(wordlist), new Language("de"));
	}
	
	@Test
	void test() {
		String wordlistStr = """
				3f0601068209354a Rat
				3efc28803f258cb7 schwierigen
				3efe194a134042e1 Landgericht
				""";
		var wordList = getWordList(wordlistStr);
		assertEquals(wordList.getLanguage(), new Language("de"));
		assertTrue(wordList.getLikelihood(new Word("Rat")) > 0);
		assertEquals(0, wordList.getLikelihood(new Word("Maus")));
	}
}