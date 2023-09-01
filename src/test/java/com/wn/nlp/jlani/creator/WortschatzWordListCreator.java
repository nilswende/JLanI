package com.wn.nlp.jlani.creator;

import com.wn.nlp.jlani.WordListCreator;
import com.wn.nlp.jlani.util.IOUtil;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

class WortschatzWordListCreator {
	public static void main(String[] args) {
		createFromWortschatzWords(Path.of("../../Wortschatz/deu_news_2014_10K-words.txt"), Path.of("./de.txt.gz"));
		createFromWortschatzWords(Path.of("../../Wortschatz/eng_news_2014_10K-words.txt"), Path.of("./en.txt.gz"));
		createFromWortschatzWords(Path.of("../../Wortschatz/fra_news_2013_10K-words.txt"), Path.of("./fr.txt.gz"));
	}
	
	private static void createFromWortschatzWords(final Path source, final Path destination) {
		if (Files.notExists(source)) {
			throw new IllegalArgumentException("Missing file: " + source.toAbsolutePath());
		}
		try (var lineReader = new LineNumberReader(IOUtil.newFileReader(source));
			 var writer = IOUtil.newFileWriter(destination, true)) {
			new WordListCreator().createFromWortschatzWords(lineReader, writer);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
