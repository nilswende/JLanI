package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.Objects;

/**
 * Maps words of a language to their likelihood.
 */
public abstract class WordList {
	private final Language language;
	
	protected WordList(final Language language) {
		this.language = language;
	}
	
	/**
	 * The likelihood of the given word.
	 *
	 * @param word the word
	 * @return the likelihood or null, if the word was not found
	 */
	public abstract Double getLikelihood(Word word);
	
	/**
	 * The language of the wordlist.
	 */
	public Language getLanguage() {
		return language;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		final WordList wordList = (WordList) o;
		return Objects.equals(language, wordList.language);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(language);
	}
	
	@Override
	public String toString() {
		return "WordList{" +
			   "language=" + language +
			   '}';
	}
}
