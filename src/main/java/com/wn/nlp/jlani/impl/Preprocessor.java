package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.Preferences;
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.value.Word;

import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;

/**
 * Preprocesses a sentence for language identification.
 */
public class Preprocessor {
	public List<Word> preprocessSentence(final Request request) {
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
