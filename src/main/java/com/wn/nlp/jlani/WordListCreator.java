package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.Preprocessor;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.stream.Collectors;

/**
 * Creates a wordlist for language identification.
 */
public class WordListCreator {
	/**
	 * Creates a wordlist from a given text.
	 *
	 * @param text   the source text
	 * @param writer the destination writer
	 */
	public void createFromText(final String text, final PrintWriter writer) {
		var words = new Preprocessor().preprocessSentence(new Request(text));
		var wordCounts = words.stream().collect(Collectors.groupingBy(w -> w, LinkedHashMap::new, Collectors.counting()));
		
		for (final var entry : wordCounts.entrySet()) {
			writer.print(entry.getValue());
			writer.print(' ');
			writer.print(entry.getKey().value());
			writer.println();
		}
	}
	
	/**
	 * Creates a wordlist from a given <a href="https://wortschatz.uni-leipzig.de/en/download">Wortschatz</a> word file.
	 *
	 * @param lineReader the source reader
	 * @param writer     the destination writer
	 */
	public void createFromWortschatzWords(final LineNumberReader lineReader, final PrintWriter writer) throws IOException {
		for (String line; (line = lineReader.readLine()) != null; ) {
			if (line.isBlank()) continue;
			var limit = 3;
			var split = line.split("\\t", limit);
			if (split.length != limit) {
				var msg = "Illegal input format on line %d: %s".formatted(lineReader.getLineNumber(), line);
				throw new IllegalArgumentException(msg);
			}
			var id = Integer.parseInt(split[0]);
			if (id <= 100) continue; // skip special characters
			var word = split[1];
			var count = split[2];
			writer.print(count);
			writer.print(' ');
			writer.print(word);
			writer.println();
		}
	}
}
