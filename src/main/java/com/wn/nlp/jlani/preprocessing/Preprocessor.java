package com.wn.nlp.jlani.preprocessing;

import com.wn.nlp.jlani.Preferences;
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.preprocessing.impl.RegexCleaner;
import com.wn.nlp.jlani.preprocessing.impl.SimpleBlacklist;
import com.wn.nlp.jlani.value.Word;
import net.jcip.annotations.Immutable;

import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.wn.nlp.jlani.Preferences.Key.BLACKLIST_FILE;
import static com.wn.nlp.jlani.Preferences.Key.SPECIAL_CHARS;

/**
 * Preprocesses strings for language identification.
 */
@Immutable
public class Preprocessor {
	private static final Pattern WORD_SEPARATOR = Pattern.compile("\\s+");
	private static final Pattern NUMBER = Pattern.compile("\\d+");
	private static final Normalizer.Form FORM = Normalizer.Form.NFKC;
	private final Cleaner cleaner = RegexCleaner.ofRegex(Preferences.INSTANCE.get(SPECIAL_CHARS));
	private final Blacklist blacklist = SimpleBlacklist.ofPath(Preferences.INSTANCE.get(BLACKLIST_FILE));
	
	/**
	 * Preprocesses a sentence for language identification.
	 *
	 * @param request the request
	 * @return the preprocessed list of words
	 */
	public List<Word> preprocess(final Request request) {
		final var sentence = request.getSentence();
		
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
	 * @return the word or null, if it was filtered
	 */
	public Word preprocessWord(final String wordStr) {
		var normalizedWord = Normalizer.isNormalized(wordStr, FORM) ? wordStr : Normalizer.normalize(wordStr, FORM);
		var cleanedWord = cleaner.apply(normalizedWord);
		if (cleanedWord.isEmpty()) return null;
		if (NUMBER.matcher(cleanedWord).matches()) return null;
		var word = new Word(cleanedWord);
		if (blacklist.contains(word)) return null;
		return word;
	}
	
	private List<Word> sampleWords(final List<Word> words, final int wordsToCheck) {
		var step = words.size() / wordsToCheck;
		var sample = new ArrayList<Word>();
		for (int i = 0; i < words.size() && sample.size() < wordsToCheck; i += step) {
			sample.add(words.get(i));
		}
		return sample;
	}
}
