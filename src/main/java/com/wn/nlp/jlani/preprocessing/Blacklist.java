package com.wn.nlp.jlani.preprocessing;

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
	boolean contains(Word word);
	
	class NullBlacklist implements Blacklist {
		@Override
		public boolean contains(final Word word) {
			return false;
		}
	}
}
