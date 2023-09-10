package com.wn.nlp.jlani.creator;

import com.wn.nlp.jlani.WordListCreator;
import com.wn.nlp.jlani.util.IOUtil;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;

class TextFileWordListCreator {
	public static void main(String[] args) {
		for (int i = 0; i < args.length; i += 2) {
			create(Path.of(args[i]), Path.of(args[i + 1]));
		}
	}
	
	private static void create(final Path source, final Path destination) {
		if (Files.notExists(source)) {
			throw new IllegalArgumentException("Missing file: " + source.toAbsolutePath());
		}
		try (var reader = IOUtil.newFileReader(source);
			 var writer = IOUtil.newFileWriter(destination, true)) {
			var stringWriter = new StringWriter();
			reader.transferTo(stringWriter);
			new WordListCreator().createFromText(stringWriter.toString(), writer);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
}
