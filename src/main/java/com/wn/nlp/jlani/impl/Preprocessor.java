package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.Preferences;
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.value.Word;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Preprocesses a sentence for language identification.
 */
public class Preprocessor {
	private static final Pattern WORD_SEPARATOR = Pattern.compile("\\s+");
	
	public List<Word> preprocessSentence(final Request request) {
		var cleaner = RegexSentenceCleaner.ofRegex(Preferences.INSTANCE.get(Preferences.SPECIAL_CHARS));
		var blacklist = InMemoryBlacklist.ofPath(Preferences.INSTANCE.get(Preferences.BLACKLIST_FILE));
		
		var sentence = request.getSentence();
		var cleanedSentence = cleaner.apply(sentence);
		var splitSentence = Arrays.asList(WORD_SEPARATOR.split(cleanedSentence));
		var limitedSentence = request.limitWords(splitSentence.size()) ? sampleWords(splitSentence, request.getWordsToCheck()) : splitSentence;
		
		return limitedSentence.stream()
				.map(Word::new)
				.filter(word -> !blacklist.isBlacklisted(word))
				.toList();
	}
	
	private List<String> sampleWords(final List<String> splitSentence, final int wordsToCheck) {
		var step = splitSentence.size() / wordsToCheck;
		var sample = new ArrayList<String>();
		for (int i = 0; i < splitSentence.size() && sample.size() < wordsToCheck; i += step) {
			sample.add(splitSentence.get(i));
		}
		return sample;
	}
}
