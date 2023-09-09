package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.InMemoryWordList;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.io.StringWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JLanITest {
	
	private Response testDeserialized(final Request request) {
		var de = DeserializedWordList.ofFile(Path.of("wordlists/de.ser.txt.gz"));
		var en = DeserializedWordList.ofFile(Path.of("wordlists/en.ser.txt.gz"));
		var jLanI = new JLanI(new WordLists(List.of(de, en)));
		return jLanI.evaluate(request);
	}
	
	@ParameterizedTest
	@MethodSource
	void testDeserialized(final Request request, final Response expected) {
		var actual = testDeserialized(request);
		
		assertEquals(expected.getMostLikelyResult().getKey(), actual.getMostLikelyResult().getKey());
		assertEquals(expected.getCheckedWords(), actual.getCheckedWords());
		
		for (final var entry : expected.getResults().entrySet()) {
			var expectedLanguage = entry.getKey();
			var expectedResult = entry.getValue();
			var actualResult = actual.getResult(expectedLanguage);
			assertNotNull(actualResult);
			assertEquals(expectedResult.getScore(), actualResult.getScore());
			assertEquals(expectedResult.getWords(), actualResult.getWords());
			assertEquals(expectedResult.getCoverage(), actualResult.getCoverage());
		}
	}
	
	static Stream<Arguments> testDeserialized() {
		var response1 = new Response(12);
		createResult(response1, new Language("de"),
				2.5831781969994968E-15,
				List.of("is", "the", "is", "the"));
		createResult(response1, new Language("en"),
				397.23624780727886,
				List.of("my", "is", "over", "the", "my", "is", "over", "the", "see"));
		var response2 = new Response(12); // originally 13, fails on empty word filter
		createResult(response2, new Language("de"),
				0.0074838949003064,
				List.of("und", "bin", "ich", "ein", "kurzer", "deutscher", "Satz"));
		createResult(response2, new Language("en"),
				9.354868625383001E-7,
				List.of("last", "but", "not", "least"));
		
		return Stream.of(
				arguments(new Request("my pony is over the ocean, my bonny is over the see"), response1),
				arguments(new Request(" und last but not least, bin ich ein _kurzer_ deutscher Satz (hubergel)!"), response2)
		);
	}
	
	static void createResult(Response response, Language language, double score, List<String> words) {
		var result = response.createResult(language);
		result.addScore(score);
		words.forEach(w -> result.addWord(new Word(w)));
	}
	
	@Test
	void testDefault() {
		var jLanI = new JLanI();
		var actual = jLanI.evaluate(new Request("identifies the most likely language of an unknown text"));
		assertEquals("en", actual.getMostLikelyResult().getKey().name());
	}
	
	@Test
	void testNormalization() {
		var composed = "\u00c1bc";
		var decomposed = "A\u0301bc";
		// create wordlist file
		var stringWriter = new StringWriter();
		new WordListCreator().createFromText(composed, stringWriter);
		// create wordlist from wordlist file
		var wordList = InMemoryWordList.ofWordCountFileReader(new StringReader(stringWriter.toString()), new Language("lang"));
		// evaluate
		var jLanI = new JLanI(new WordLists(List.of(wordList)));
		var actual = jLanI.evaluate(new Request(decomposed));
		
		var actualResult = actual.getMostLikelyResult();
		assertTrue(actualResult.getValue().getWords().stream()
				.anyMatch(w -> w.value().equals(composed) || w.value().equals(decomposed)));
	}
}