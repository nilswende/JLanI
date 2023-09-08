package com.wn.nlp.jlani;

/**
 * Removes unwanted characters from a sentence.
 */
public interface SentenceCleaner {
	/**
	 * Removes unwanted characters from a sentence.
	 *
	 * @param sentence the sentence
	 * @return a sentence containing no unwanted characters
	 */
	String apply(CharSequence sentence);
	
	/**
	 * Removes no characters.
	 */
	class NullSentenceCleaner implements SentenceCleaner {
		@Override
		public String apply(final CharSequence sentence) {
			return sentence.toString();
		}
	}
}
