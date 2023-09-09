package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PreprocessorTest {
	@Test
	void testSampling() {
		var wordsToCheck = 5;
		var request = new Request("JLanI identifies the most likely language of an unknown text", Set.of(), wordsToCheck);
		var actual = new Preprocessor().preprocess(request);
		assertEquals(wordsToCheck, actual.size());
		assertEquals("JLanI the likely of unknown", actual.stream().map(Word::value).collect(Collectors.joining(" ")));
	}
	
	@Test
	void testSamplingAll() {
		var sentence = "JLanI identifies the most likely language of an unknown text";
		var request = new Request(sentence, Set.of(), 20);
		var actual = new Preprocessor().preprocess(request);
		assertEquals(10, actual.size());
		assertEquals(sentence, actual.stream().map(Word::value).collect(Collectors.joining(" ")));
	}
	
	@Test
	void testSamplingNone() {
		var sentence = "JLanI identifies the most likely language of an unknown text";
		var request = new Request(sentence);
		var actual = new Preprocessor().preprocess(request);
		assertEquals(10, actual.size());
		assertEquals(sentence, actual.stream().map(Word::value).collect(Collectors.joining(" ")));
	}
}