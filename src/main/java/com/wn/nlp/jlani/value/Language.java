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
		if (name.isBlank()) throw new IllegalArgumentException("name is blank");
		name = name.toLowerCase();
	}
	
	/**
	 * Extracts the language name from the file name of a path.
	 *
	 * @param path the path
	 * @return the language
	 */
	public static Language ofPath(final Path path) {
		var fileName = path.getFileName().toString();
		return ofFileName(fileName);
	}
	
	/**
	 * Extracts the language name from the file name.
	 *
	 * @param fileName the file name
	 * @return the language
	 */
	public static Language ofFileName(final String fileName) {
		var languageName = fileName.split("\\.")[0];
		return new Language(languageName);
	}
}
