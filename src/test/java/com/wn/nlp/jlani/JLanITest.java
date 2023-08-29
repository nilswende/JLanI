package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.InMemoryWordList;
import com.wn.nlp.jlani.impl.JLanIImpl;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.params.provider.Arguments.arguments;

class JLanITest {
	
	private Response evaluate(final Request request) {
		var wordLists = new WordLists();
		wordLists.addWordList(InMemoryWordList.ofSerializedFile(Path.of("./resources/wordlists/de.txt")));
		wordLists.addWordList(InMemoryWordList.ofSerializedFile(Path.of("./resources/wordlists/en.txt")));
		var jLanI = new JLanIImpl(wordLists);
		return jLanI.evaluate(request);
	}
	
	@ParameterizedTest
	@MethodSource
	void evaluate(final Request request, final Response expected) {
		var actual = evaluate(request);
		for (final var entry : expected.getResults().entrySet()) {
			var expectedLanguage = entry.getKey();
			var expectedResult = entry.getValue();
			var actualResult = actual.getResults().get(expectedLanguage);
			assertNotNull(actualResult);
			assertEquals(expectedResult.getScore(), actualResult.getScore());
			assertEquals(expectedResult.getWords(), actualResult.getWords());
			assertEquals(expectedResult.getCoverage(), actualResult.getCoverage());
		}
		assertEquals(expected.getMostLikelyResult().getKey(), actual.getMostLikelyResult().getKey());
		assertEquals(expected.getCheckedWords(), actual.getCheckedWords());
	}
	
	static Stream<Arguments> evaluate() {
		return Stream.of(
				arguments(new Request("my pony is over the ocean, my bonny is over the see"),
						new TestResponse(Map.of(
								new Language("de"),
								new TestResponse.TestResult(2.5831781969994968E-15,
										List.of("is", "the", "is", "the"),
										12),
								new Language("en"),
								new TestResponse.TestResult(397.23624780727886,
										List.of("my", "is", "over", "the", "my", "is", "over", "the", "see"),
										12)
						),
								12)),
				arguments(new Request(" und last but not least, bin ich ein _kurzer_ deutscher Satz (hubergel)!"),
						new TestResponse(Map.of(
								new Language("de"),
								new TestResponse.TestResult(0.0074838949003064,
										List.of("und", "bin", "ich", "ein", "kurzer", "deutscher", "Satz"),
										13),
								new Language("en"),
								new TestResponse.TestResult(9.354868625383001E-7,
										List.of("last", "but", "not", "least"),
										13)
						),
								13))
		);
	}
	
	static class TestResponse extends Response {
		public TestResponse(final Map<Language, Result> results, int checkedWords) {
			super(results, checkedWords);
		}
		
		static class TestResult extends Result {
			public TestResult(double score, List<String> words, int checkedWords) {
				super(checkedWords);
				addScore(score);
				words.forEach(w -> addWord(new Word(w)));
			}
		}
	}
}