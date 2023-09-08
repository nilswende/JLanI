package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JLanITest {
	
	private Response evaluateDeserialized(final Request request) {
		var wordLists = new WordLists();
		wordLists.addWordList(DeserializedWordList.ofFile(Path.of("wordlists/de.ser.txt.gz")));
		wordLists.addWordList(DeserializedWordList.ofFile(Path.of("wordlists/en.ser.txt.gz")));
		var jLanI = new JLanI(wordLists);
		return jLanI.evaluate(request);
	}
	
	@ParameterizedTest
	@MethodSource
	void evaluateDeserialized(final Request request, final Response expected) {
		var actual = evaluateDeserialized(request);
		for (final var entry : expected.getResults().entrySet()) {
			var expectedLanguage = entry.getKey();
			var expectedResult = entry.getValue();
			var actualResult = actual.getResult(expectedLanguage);
			assertNotNull(actualResult);
			assertEquals(expectedResult.getScore(), actualResult.getScore());
			assertEquals(expectedResult.getWords(), actualResult.getWords());
			assertEquals(expectedResult.getCoverage(), actualResult.getCoverage());
		}
		assertEquals(expected.getMostLikelyResult().getKey(), actual.getMostLikelyResult().getKey());
		assertEquals(expected.getCheckedWords(), actual.getCheckedWords());
	}
	
	static Stream<Arguments> evaluateDeserialized() {
		var response1 = new Response(12);
		createResult(response1, new Language("de"),
				2.5831781969994968E-15,
				List.of("is", "the", "is", "the"));
		createResult(response1, new Language("en"),
				397.23624780727886,
				List.of("my", "is", "over", "the", "my", "is", "over", "the", "see"));
		var response2 = new Response(13);
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
	void evaluateDefault() {
		var jLanI = new JLanI();
		var actual = jLanI.evaluate(new Request("identifies the most likely language of an unknown text"));
		assertEquals("en", actual.getMostLikelyResult().getKey().name());
	}
}