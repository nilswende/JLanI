package com.wn.nlp.jlani.value;

import java.util.Objects;

/**
 * A word of a sentence.
 *
 * @param value characters of the word
 */
public record Word(
		String value
) {
	public Word {
		Objects.requireNonNull(value);
	}
}
