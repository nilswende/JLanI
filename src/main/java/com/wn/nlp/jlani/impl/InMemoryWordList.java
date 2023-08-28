package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Maps words of a language to their likelihood using an in-memory word list.
 */
public class InMemoryWordList implements WordList {
	private final Map<Word, Double> map;
	private final Language language;
	
	public InMemoryWordList(final Map<Word, Double> map, final Language language) {
		this.map = map;
		this.language = language;
	}
	
	/**
	 * Creates a new InMemoryWordList from a formerly serialized wordlist file dump.
	 *
	 * @param path Path to the wordlist file. Its name is expected to be the language name, e.g. {@code en.txt}.
	 */
	public static InMemoryWordList ofSerializedFile(final Path path) {
		var languageName = path.getFileName().toString().split("\\.")[0];
		var language = new Language(languageName);
		try (var reader = new InputStreamReader(Files.newInputStream(path))) {
			return ofSerializedFileReader(reader, language);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Creates a new InMemoryWordList from a formerly serialized wordlist file dump.
	 *
	 * @param reader   the content of the wordlist file
	 * @param language the language of the wordlist file
	 */
	public static InMemoryWordList ofSerializedFileReader(final Reader reader, final Language language) {
		var map = new HashMap<Word, Double>();
		try (var lineReader = new LineNumberReader(reader)) {
			for (String line; (line = lineReader.readLine()) != null; ) {
				if (line.isBlank()) continue;
				var limit = 2;
				var split = line.split(" ", limit);
				if (split.length != limit) {
					var msg = "Illegal input format on line %d: %s".formatted(lineReader.getLineNumber(), line);
					throw new IllegalArgumentException(msg);
				}
				var d = Double.longBitsToDouble(Long.parseUnsignedLong(split[0], 16));
				map.put(new Word(split[1]), d);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return new InMemoryWordList(map, language);
	}
	
	@Override
	public double getLikelihood(final Word word) {
		var v = map.get(word);
		return v == null ? 0 : v;
	}
	
	@Override
	public Language getLanguage() {
		return language;
	}
}
