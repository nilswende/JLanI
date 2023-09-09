package com.wn.nlp.jlani;

import com.wn.nlp.jlani.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordListCreatorTest {
	
	@Test
	void createFromText() {
		var text = """
				any any text
				""";
		var stringWriter = new StringWriter();
		try (var writer = new PrintWriter(stringWriter)) {
			new WordListCreator().createFromText(text, writer);
		}
		var expected = """
				2 any
				1 text
				""";
		assertEquals(expected, stringWriter.toString().replaceAll("\\r\\n|\\r", "\n"));
	}
	
	@Test
	void createFromWortschatzWords() {
		var stringWriter = new StringWriter();
		try (var lineReader = new LineNumberReader(IOUtil.newResourceReader(Path.of("en.ws.txt")));
			 var writer = new PrintWriter(stringWriter)) {
			new WordListCreator().createFromWortschatzWords(lineReader, writer);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		var expected = """
				2 any
				1 text
				""";
		assertEquals(expected, stringWriter.toString().replaceAll("\\r\\n|\\r", "\n"));
	}
}