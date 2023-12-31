package com.wn.nlp.jlani.preprocessing.impl;

import com.wn.nlp.jlani.preprocessing.Blacklist;
import com.wn.nlp.jlani.value.Word;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * Flags words as unwanted.
 */
public class SimpleBlacklist implements Blacklist {
	private static final Logger LOGGER = LoggerFactory.getLogger(SimpleBlacklist.class);
	private final Set<Word> blacklist;
	
	private SimpleBlacklist(final Set<Word> blacklist) {
		this.blacklist = blacklist;
	}
	
	/**
	 * Creates a new Blacklist from a list of words.
	 *
	 * @param pathString the path to a blacklist file
	 */
	public static Blacklist ofPath(final String pathString) {
		if (pathString == null) {
			return new NullBlacklist();
		} else if (pathString.isBlank()) {
			LOGGER.info("Ignored empty path");
			return new NullBlacklist();
		}
		var path = Path.of(pathString);
		if (Files.notExists(path)) {
			LOGGER.warn("Ignored missing path: {}", path.toAbsolutePath());
			return new NullBlacklist();
		}
		try (var reader = new InputStreamReader(Files.newInputStream(path))) {
			return ofReader(reader);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Creates a new Blacklist from a list of words.
	 *
	 * @param reader the blacklisted words
	 */
	public static Blacklist ofReader(final Reader reader) {
		var set = new HashSet<Word>();
		try (var lineReader = new BufferedReader(reader)) {
			for (String line; (line = lineReader.readLine()) != null; ) {
				if (line.isBlank()) continue;
				set.add(new Word(line));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return new SimpleBlacklist(set);
	}
	
	@Override
	public boolean contains(final Word word) {
		return blacklist.contains(word);
	}
}
