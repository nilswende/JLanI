package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.DeserializedWordList;
import com.wn.nlp.jlani.impl.SimpleWordList;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class WordListTest {
	private WordList getWordList(final String wordlist) {
		return DeserializedWordList.ofFileReader(new StringReader(wordlist), new Language("de"));
	}
	
	@Test
	void testEquals() {
		assertEquals(new MockWordList("de"), new MockWordList("de"));
	}
	
	@Test
	void testString() {
		String wordlistStr = """
				3f0601068209354a Rat
				3efc28803f258cb7 schwierigen
				3efe194a134042e1 Landgericht
				""";
		var wordList = getWordList(wordlistStr);
		assertEquals(wordList.getLanguage(), new Language("de"));
		assertTrue(wordList.getLikelihood(new Word("Rat")) > 0);
		assertNull(wordList.getLikelihood(new Word("Maus")));
	}
	
	@Test
	void testIllegalReaderFormat() {
		String wordlistStr = """
				3f0601068209354a
				3efc28803f258cb7
				3efe194a134042e1
				""";
		assertThrows(IllegalArgumentException.class, () -> getWordList(wordlistStr));
	}
	
	@Test
	void testIllegalNumber() {
		String wordlistStr = """
				-2 Rat
				""";
		assertThrows(IllegalArgumentException.class, () -> SimpleWordList.ofWordlistFileReader(new StringReader(wordlistStr), new Language("de")));
	}
	
	@Test
	void testWordCountFile() {
		var path = Path.of("./src/test/resources/en.txt");
		var wordList = SimpleWordList.ofWordlistFile(path);
		assertEquals(wordList.getLanguage(), new Language("en"));
		assertEquals(0.5, wordList.getLikelihood(new Word("prepare")));
		assertNull(wordList.getLikelihood(new Word("full")));
	}
	
	@Test
	void testMissingWordCountFile() {
		var path = Path.of("./resources/wordlists/missing.txt");
		assertThrows(IllegalArgumentException.class, () -> SimpleWordList.ofWordlistFile(path));
	}
	
	static class MockWordList extends WordList {
		protected MockWordList(final String language) {
			super(new Language(language));
		}
		
		@Override
		public boolean containsWord(final Word word) {
			return false;
		}
		
		@Override
		public Double getLikelihood(final Word word) {
			return null;
		}
	}
}