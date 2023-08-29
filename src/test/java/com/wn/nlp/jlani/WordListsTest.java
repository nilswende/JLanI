package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordListsTest {
	@Test
	void testNoneAvailable() {
		var wordLists = new WordLists();
		assertThrows(IllegalStateException.class, () -> wordLists.getEvaluatedWordLists(Set.of()));
	}
	
	@Test
	void testUnknownRequested() {
		var wordLists = new WordLists();
		wordLists.addWordList(new WordListTest.MockWordList("de"));
		assertThrows(IllegalArgumentException.class, () -> wordLists.getEvaluatedWordLists(Set.of(new Language("en"))));
	}
	
	@Test
	void testSpecificRequested() {
		var wordLists = new WordLists();
		wordLists.addWordList(new WordListTest.MockWordList("de"));
		wordLists.addWordList(new WordListTest.MockWordList("en"));
		var actual = wordLists.getEvaluatedWordLists(Set.of(new Language("en")));
		assertEquals(Set.of(new Language("en")), actual.keySet());
	}
}