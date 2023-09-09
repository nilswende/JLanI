package com.wn.nlp.jlani;

import com.wn.nlp.jlani.util.IOUtil;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UncheckedIOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WordListCreatorTest {
	
	@Test
	void createFromText() {
		var text = """
				any any text
				""";
		var stringWriter = new StringWriter();
		new WordListCreator().createFromText(text, stringWriter);
		var expected = """
				2 any
				1 text
				""";
		assertEquals(expected, stringWriter.toString().replaceAll("\\r\\n|\\r", "\n"));
	}
	
	@Test
	void createFromWortschatzWords() {
		var stringWriter = new StringWriter();
		try (var reader = IOUtil.newResourceReader(Path.of("en.ws.txt"))) {
			new WordListCreator().createFromWortschatzWords(reader, stringWriter);
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