package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.InMemoryWordList;
import com.wn.nlp.jlani.util.IOUtil;
import com.wn.nlp.jlani.value.Language;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Path;

class DeserializedWordList {
	/**
	 * Creates a new WordList from a deserialized wordlist file dump.<br>
	 * The file format is: {@code (<hex> <word>)*}
	 *
	 * @param path Path to the wordlist file. Its name is expected to be the language name, e.g. {@code en.ser.txt.gz}.
	 */
	public static WordList ofFile(final Path path) {
		try (var reader = IOUtil.newResourceReader(path)) {
			var languageName = path.getFileName().toString().split("\\.")[0];
			var language = new Language(languageName);
			return ofFileReader(reader, language);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Creates a new WordList from a deserialized wordlist file dump.<br>
	 * The file format is: {@code (<hex> <word>)*}
	 *
	 * @param reader   the content of the wordlist file
	 * @param language the language of the wordlist file
	 */
	public static WordList ofFileReader(final Reader reader, final Language language) {
		var map = InMemoryWordList.ofReader(reader, s -> Double.longBitsToDouble(Long.parseUnsignedLong(s, 16)));
		return new InMemoryWordList(map, language);
	}
}
