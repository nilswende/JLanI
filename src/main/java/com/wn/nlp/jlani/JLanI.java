package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.Preprocessor;
import com.wn.nlp.jlani.impl.Scoring;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import net.jcip.annotations.ThreadSafe;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Java Language Identifier.<br>
 * Identifies the most likely language of an unknown text.
 */
@ThreadSafe
public class JLanI {
	private final Preprocessor preprocessor = new Preprocessor();
	private final WordLists wordLists;
	
	public JLanI(final WordLists wordLists) {
		this.wordLists = Objects.requireNonNull(wordLists);
	}
	
	public JLanI() {
		this(WordLists.ofFiles());
	}
	
	public Response evaluate(final Request request) {
		Objects.requireNonNull(request);
		var evaluatedWordLists = wordLists.getEvaluatedWordLists(request.getLanguages());
		var sentence = preprocessor.preprocess(request);
		
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
