package com.wn.nlp.jlani.preprocessing;

/**
 * Removes unwanted characters from a string.
 */
public interface Cleaner {
	/**
	 * Removes unwanted characters from a string.
	 *
	 * @param string the string
	 * @return a string containing no unwanted characters
	 */
	String apply(String string);
	
	/**
	 * Removes no characters.
	 */
	class NullCleaner implements Cleaner {
		@Override
		public String apply(final String string) {
			return string;
		}
	}
}
