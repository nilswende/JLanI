package com.wn.nlp.jlani.value;

import java.nio.file.Path;
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
	
	public static Language ofPath(final Path path) {
		var fileName = path.getFileName().toString();
		return ofFileName(fileName);
	}
	
	public static Language ofFileName(final String fileName) {
		var languageName = fileName.split("\\.")[0];
		return new Language(languageName);
	}
}
