package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.WordList;
import com.wn.nlp.jlani.util.IOUtil;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;
import net.jcip.annotations.Immutable;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.ToDoubleFunction;

/**
 * Maps words of a language to their likelihood.
 */
@Immutable
public class SimpleWordList extends WordList {
	private final Map<Word, Double> wordlist;
	
	SimpleWordList(final Map<Word, Double> wordlist, final Language language) {
		super(language);
		this.wordlist = Objects.requireNonNull(wordlist);
	}
	
	static Map<Word, Double> ofReader(final Reader reader, final ToDoubleFunction<String> transform) {
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
				map.put(new Word(split[1]), transform.applyAsDouble(split[0]));
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		return map;
	}
	
	/**
	 * Creates an immutable WordList from a wordlist file containing word counts.<br>
	 * The file format is: {@code (<int> <word>)*}
	 *
	 * @param path Path to the wordlist file. Its name is expected to be the language name, e.g. {@code en.txt.gz}.
	 */
	public static WordList ofWordlistFile(final Path path) {
		if (Files.notExists(path)) {
			throw new IllegalArgumentException("Missing file: " + path.toAbsolutePath());
		}
		try (var reader = IOUtil.newFileReader(path)) {
			return ofWordlistFileReader(reader, Language.ofPath(path));
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Creates an immutable WordList from a wordlist file containing word counts.<br>
	 * The file format is: {@code (<int> <word>)*}
	 *
	 * @param reader   the content of the wordlist file
	 * @param language the language of the wordlist file
	 */
	public static WordList ofWordlistFileReader(final Reader reader, final Language language) {
		var map = ofReader(reader, Integer::parseInt);
		if (map.values().stream().anyMatch(d -> d < 1)) {
			throw new IllegalArgumentException("any word count must be 1 or higher");
		}
		normalize(map);
		return new SimpleWordList(map, language);
	}
	
	private static void normalize(final Map<?, Double> map) {
		var sum = map.values().stream()
				.mapToDouble(d -> d)
				.sum();
		map.replaceAll((w, d) -> d / sum);
	}
	
	@Override
	public boolean containsWord(final Word word) {
		return wordlist.containsKey(word);
	}
	
	@Override
	public Double getLikelihood(final Word word) {
		return wordlist.get(word);
	}
}
