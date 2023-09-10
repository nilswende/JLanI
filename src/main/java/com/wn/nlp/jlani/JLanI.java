package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.Scoring;
import com.wn.nlp.jlani.preprocessing.Preprocessor;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import net.jcip.annotations.Immutable;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java Language Identifier.<br>
 * Identifies the most likely language of an unknown text.
 */
@Immutable
public class JLanI {
	private final Preprocessor preprocessor = new Preprocessor();
	private final WordLists wordLists;
	
	/**
	 * Instances created by this constructor are immutable if the supplied {@link WordLists} instance is.
	 */
	public JLanI(final WordLists wordLists) {
		this.wordLists = Objects.requireNonNull(wordLists);
	}
	
	/**
	 * Creates an immutable instance.
	 */
	public JLanI() {
		this(WordLists.ofFiles());
	}
	
	public Response evaluate(final Request request) {
		Objects.requireNonNull(request);
		var evaluatedWordLists = wordLists.getEvaluatedWordLists(request.getLanguages());
		var words = preprocessor.preprocess(request);
		
		var response = new Response(evaluatedWordLists.keySet(), words.size());
		
		evaluateScore(evaluatedWordLists, words, response);
		evaluateWords(evaluatedWordLists, words, response);
		
		return response;
	}
	
	private void evaluateScore(final Map<Language, WordList> evaluatedWordLists, final List<Word> words, final Response response) {
		var scoring = new Scoring(evaluatedWordLists);
		for (final var word : words) {
			var scores = scoring.evaluate(word);
			scores.forEach((language, score) -> response.getResult(language).addScore(score));
		}
	}
	
	private void evaluateWords(final Map<Language, WordList> evaluatedWordLists, final List<Word> words, final Response response) {
		evaluatedWordLists.forEach((language, wordList) -> {
			var result = response.getResult(language);
			words.stream().filter(wordList::containsWord).forEach(result::addWord);
		});
	}
}
