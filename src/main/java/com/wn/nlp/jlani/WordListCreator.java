package com.wn.nlp.jlani;

import com.wn.nlp.jlani.preprocessing.Preprocessor;
import com.wn.nlp.jlani.value.Word;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;
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
	public void createFromText(final String text, final Writer writer) {
		var words = new Preprocessor().preprocess(new Request(text));
		var wordCounts = words.stream().map(Word::value)
				.collect(Collectors.groupingBy(w -> w, LinkedHashMap::new, Collectors.counting()));
		
		printWordCounts(wordCounts, writer);
	}
	
	private void printWordCounts(final Map<String, Long> wordCounts, final Writer writer) {
		var printWriter = writer instanceof PrintWriter ? ((PrintWriter) writer) : new PrintWriter(writer);
		for (final var entry : wordCounts.entrySet()) {
			printWriter.print(entry.getValue());
			printWriter.print(' ');
			printWriter.print(entry.getKey());
			printWriter.println();
		}
		printWriter.flush();
	}
	
	/**
	 * Creates a wordlist from a given <a href="https://wortschatz.uni-leipzig.de/en/download">Wortschatz</a> word file.
	 *
	 * @param reader the source reader
	 * @param writer the destination writer
	 */
	public void createFromWortschatzWords(final Reader reader, final Writer writer) throws IOException {
		var preprocessor = new Preprocessor();
		var wordCounts = new LinkedHashMap<String, Long>();
		var lineReader = new LineNumberReader(reader);
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
			var word = preprocessor.preprocessWord(split[1]);
			if (word == null) continue;
			var count = Long.parseLong(split[2]);
			wordCounts.merge(word.value(), count, Long::sum);
		}
		
		printWordCounts(wordCounts, writer);
	}
}
