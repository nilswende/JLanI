package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class DeserializedWordListTest {
	@Test
	void testSerializedFile() {
		var path = Path.of("wordlists/de.ser.txt.gz");
		var wordList = DeserializedWordList.ofFile(path);
		assertEquals(wordList.getLanguage(), new Language("de"));
		assertTrue(wordList.getLikelihood(new Word("Rat")) > 0);
		assertNull(wordList.getLikelihood(new Word("Maus")));
	}
	
	@Test
	void testMissingSerializedFile() {
		var path = Path.of("./resources/wordlists/missing.txt");
		assertThrows(NullPointerException.class, () -> DeserializedWordList.ofFile(path));
	}
}