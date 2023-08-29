package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.value.Word;

/**
 * Flags words as unwanted.
 */
public interface Blacklist {
	/**
	 * Flags a word as unwanted.
	 *
	 * @param word the word
	 * @return true, if the word is unwanted
	 */
	boolean isBlacklisted(Word word);
	
	class NullBlacklist implements Blacklist {
		@Override
		public boolean isBlacklisted(final Word word) {
			return false;
		}
	}
}
