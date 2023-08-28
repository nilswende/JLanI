package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

/**
 * Maps words of a language to their likelihood.
 */
public interface WordList {
	/**
	 * The likelihood of the given word.
	 *
	 * @param word the word
	 * @return the likelihood or 0, if the word was not found
	 */
	double getLikelihood(Word word);
	
	/**
	 * The language of the wordlist.
	 */
	Language getLanguage();
}
