package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.InMemoryWordList;
import com.wn.nlp.jlani.impl.Preprocessor;
import com.wn.nlp.jlani.impl.Scoring;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java Language Identifier.<br>
 * Identifies the most likely language of an unknown text.
 */
public class JLanI {
	private final WordLists wordLists;
	
	public JLanI(final WordLists wordLists) {
		this.wordLists = Objects.requireNonNull(wordLists);
	}
	
	public JLanI() {
		this(createWordLists());
	}
	
	private static WordLists createWordLists() {
		var wordlistDirStr = Preferences.INSTANCE.get(Preferences.WORDLIST_DIR);
		if (wordlistDirStr == null || wordlistDirStr.isBlank()) {
			throw new IllegalArgumentException("%s needs to be set in the config file".formatted(Preferences.WORDLIST_DIR));
		}
		var wordlistDir = Path.of(wordlistDirStr);
		if (Files.notExists(wordlistDir)) {
			throw new IllegalArgumentException("Missing file: " + wordlistDir.toAbsolutePath());
		}
		try (var paths = Files.walk(wordlistDir)) {
			var wordLists = paths.filter(Files::isRegularFile).map(InMemoryWordList::ofWordCountFile).toList();
			return new WordLists(wordLists);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public Response evaluate(final Request request) {
		Objects.requireNonNull(request);
		var evaluatedWordLists = wordLists.getEvaluatedWordLists(request.getLanguages());
		var sentence = new Preprocessor().preprocessSentence(request);
		
		var response = new Response(evaluatedWordLists.keySet(), sentence.size());
		
		evaluateScore(evaluatedWordLists, sentence, response);
		evaluateWords(evaluatedWordLists, sentence, response);
		
		return response;
	}
	
	private void evaluateScore(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		var scoring = new Scoring(evaluatedWordLists);
		for (final var word : sentence) {
			var scores = scoring.evaluate(word);
			scores.forEach((language, score) -> response.getResult(language).addScore(score));
		}
	}
	
	private void evaluateWords(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		evaluatedWordLists.forEach((language, wordList) -> {
			var result = response.getResult(language);
			sentence.stream().filter(wordList::containsWord).forEach(result::addWord);
		});
	}
}
