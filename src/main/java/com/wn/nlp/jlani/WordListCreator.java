package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.Preprocessor;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Collectors;

/**
 * Creates a wordlist for language identification from a given text.
 */
public class WordListCreator {
	public void create(final Request request, final Path path) {
		var words = new Preprocessor().preprocessSentence(request);
		var wordCounts = words.stream().collect(Collectors.groupingBy(w -> w, Collectors.counting()));
		
		try (var writer = new PrintWriter(new BufferedOutputStream(Files.newOutputStream(path)))) {
			for (final var entry : wordCounts.entrySet()) {
				writer.print(entry.getValue());
				writer.print(' ');
				writer.print(entry.getKey().value());
				writer.println();
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	public static void main(String[] args) {
		var text = """
				identifies the most likely language of an unknown text
				""";
		new WordListCreator().create(new Request(text), Path.of("./lang.txt"));
	}
}
