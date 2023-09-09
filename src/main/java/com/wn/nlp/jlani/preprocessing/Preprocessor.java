package com.wn.nlp.jlani.preprocessing;

import com.wn.nlp.jlani.Preferences;
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.preprocessing.impl.InMemoryBlacklist;
import com.wn.nlp.jlani.preprocessing.impl.RegexSentenceCleaner;
import com.wn.nlp.jlani.value.Word;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Preprocesses a sentence for language identification.
 */
public class Preprocessor {
	private static final Pattern WORD_SEPARATOR = Pattern.compile("\\s+");
	private static final Pattern NUMBER = Pattern.compile("\\d+");
	private static final Normalizer.Form FORM = Normalizer.Form.NFKD;
	private final SentenceCleaner cleaner = RegexSentenceCleaner.ofRegex(Preferences.INSTANCE.get(Preferences.SPECIAL_CHARS));
	private final Blacklist blacklist = InMemoryBlacklist.ofPath(Preferences.INSTANCE.get(Preferences.BLACKLIST_FILE));
	
	/**
	 * Preprocesses a sentence for language identification.
	 *
	 * @param request the request
	 * @return the preprocessed list of words
	 */
	public List<Word> preprocess(final Request request) {
		final String sentence = request.getSentence();
		
		var wordStrings = Arrays.asList(WORD_SEPARATOR.split(sentence));
		var words = wordStrings.stream()
				.map(this::preprocessWord)
				.filter(Objects::nonNull)
				.toList();
		
		return request.limitWords(words.size()) ? sampleWords(words, request.getWordsToCheck()) : words;
	}
	
	/**
	 * Preprocesses a word for language identification.
	 *
	 * @param wordStr the word string
	 * @return the word or null, if it is filtered
	 */
	public Word preprocessWord(final String wordStr) {
		var normalizedWord = Normalizer.isNormalized(wordStr, FORM) ? wordStr : Normalizer.normalize(wordStr, FORM);
		var cleanedWord = cleaner.apply(normalizedWord);
		if (cleanedWord.isEmpty()) return null;
		if (NUMBER.matcher(cleanedWord).matches()) return null;
		var word = new Word(cleanedWord);
		if (blacklist.isBlacklisted(word)) return null;
		return word;
	}
	
	private List<Word> sampleWords(final List<Word> splitSentence, final int wordsToCheck) {
		var step = splitSentence.size() / wordsToCheck;
		var sample = new ArrayList<Word>();
		for (int i = 0; i < splitSentence.size() && sample.size() < wordsToCheck; i += step) {
			sample.add(splitSentence.get(i));
		}
		return sample;
	}
}
