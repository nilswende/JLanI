package com.wn.nlp.jlani.value;

import java.util.Objects;

/**
 * A language.
 *
 * @param name name of the language
 */
public record Language(String name) {
	public Language {
		Objects.requireNonNull(name);
		if (name.isBlank()) throw new IllegalArgumentException("name must not be blank");
		name = name.toLowerCase();
	}
}
