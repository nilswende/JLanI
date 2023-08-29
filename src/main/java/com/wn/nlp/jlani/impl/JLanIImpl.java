package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.*;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.IntStream;

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
		var sentence = preprocessSentence(request);
		
		var response = new Response(evaluatedWordLists.keySet(), sentence.size());
		
		evaluateScore(evaluatedWordLists, sentence, response);
		evaluateWords(evaluatedWordLists, sentence, response);
		
		return response;
	}
	
	private void evaluateScore(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		var evaluation = new Evaluation(evaluatedWordLists);
		for (final var word : sentence) {
			var evaluated = evaluation.evaluate(word);
			for (final var entry : evaluated.entrySet()) {
				var language = entry.getKey();
				var result = response.getResult(language);
				result.addScore(entry.getValue());
			}
		}
	}
	
	private void evaluateWords(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		for (final var entry : evaluatedWordLists.entrySet()) {
			var language = entry.getKey();
			var wordList = entry.getValue();
			var result = response.getResult(language);
			for (final var word : sentence) {
				if (wordList.containsWord(word)) {
					result.addWord(word);
				}
			}
		}
	}
	
	private List<Word> preprocessSentence(final Request request) {
		var cleaner = RegexSentenceCleaner.ofRegex(Preferences.INSTANCE.get(Preferences.SPECIAL_CHARS));
		var blacklist = InMemoryBlacklist.ofPath(Preferences.INSTANCE.get(Preferences.BLACKLIST_FILE));
		
		var sentence = request.getSentence();
		var cleanedSentence = cleaner.apply(sentence);
		var splitSentence = Arrays.asList(cleanedSentence.split(" "));
		var limitedSentence = request.limitWords(splitSentence.size()) ? sampleWords(splitSentence, request.getWordsToCheck()) : splitSentence;
		
		return limitedSentence.stream()
				.map(Word::new)
				.filter(word -> !blacklist.isBlacklisted(word))
				.toList();
	}
	
	private List<String> sampleWords(final List<String> splitSentence, final int wordsToCheck) {
		var step = splitSentence.size() / wordsToCheck;
		return IntStream.iterate(0, i -> i < wordsToCheck, i -> i + step)
				.mapToObj(splitSentence::get)
				.toList();
	}
}
