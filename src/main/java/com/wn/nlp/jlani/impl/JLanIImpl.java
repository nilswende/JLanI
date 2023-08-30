package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.*;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The default JLanI implementation.
 */
public class JLanIImpl implements JLanI {
	private final WordLists wordLists;
	
	public JLanIImpl(final WordLists wordLists) {
		this.wordLists = Objects.requireNonNull(wordLists);
	}
	
	@Override
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
