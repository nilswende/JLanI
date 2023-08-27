package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;

import java.util.Objects;
import java.util.Set;

/**
 * A request for language identification.
 */
public class Request {
	private final String sentence;
	private final Set<Language> languages;
	private final int wordsToCheck;
	
	/**
	 * Creates a request.<br>
	 * The whole sentence and all available languages will be considered.
	 *
	 * @param sentence The sentence whose language should be identified.
	 */
	public Request(final String sentence) {
		this(sentence, Set.of(), 0);
	}
	
	/**
	 * Creates a request.
	 *
	 * @param sentence     The sentence whose language should be identified.
	 * @param languages    The languages that should be considered. Leave empty to have all available languages considered.
	 * @param wordsToCheck The number of words to consider from the sentence. Any number below {@code 1} will cause all words to be considered.
	 */
	public Request(final String sentence, final Set<Language> languages, final int wordsToCheck) {
		this.sentence = Objects.requireNonNull(sentence);
		this.languages = Objects.requireNonNull(languages);
		this.wordsToCheck = wordsToCheck;
	}
	
	public String getSentence() {
		return sentence;
	}
	
	public Set<Language> getLanguages() {
		return languages;
	}
	
	public int getWordsToCheck() {
		return wordsToCheck;
	}
}
