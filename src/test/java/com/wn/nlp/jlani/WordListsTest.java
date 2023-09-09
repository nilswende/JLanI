package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class WordListsTest {
	@Test
	void testNoneAvailable() {
		var wordLists = new WordLists(List.of());
		assertThrows(IllegalStateException.class, () -> wordLists.getEvaluatedWordLists(Set.of()));
	}
	
	@Test
	void testUnknownRequested() {
		var wordLists = new WordLists(List.of(new WordListTest.MockWordList("de")));
		assertThrows(IllegalArgumentException.class, () -> wordLists.getEvaluatedWordLists(Set.of(new Language("en"))));
	}
	
	@Test
	void testSpecificRequested() {
		List<WordList> mockWordLists = List.of(
				new WordListTest.MockWordList("de"),
				new WordListTest.MockWordList("en")
		);
		var wordLists = new WordLists(mockWordLists);
		var actual = wordLists.getEvaluatedWordLists(Set.of(new Language("en")));
		assertEquals(Set.of(new Language("en")), actual.keySet());
	}
	
	@Test
	void testOverwriting() {
		List<WordList> mockWordLists = List.of(
				new WordListTest.MockWordList("de"),
				new WordListTest.MockWordList("de")
		);
		var wordLists = new WordLists(mockWordLists);
		var actual = wordLists.getEvaluatedWordLists(Set.of(new Language("de")));
		assertEquals(Set.of(new Language("de")), actual.keySet());
	}
}