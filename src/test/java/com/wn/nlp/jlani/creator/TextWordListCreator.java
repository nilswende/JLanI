package com.wn.nlp.jlani.creator;

import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.WordListCreator;
import com.wn.nlp.jlani.util.IOUtil;

import java.io.IOException;
import java.nio.file.Path;

class TextWordListCreator {
	public static void main(String[] args) throws IOException {
		var text = """
				any text representing the target language as best it can
				""";
		try (var writer = IOUtil.newFileWriter(Path.of("./lang.txt"), true)) {
			new WordListCreator().createFromText(new Request(text), writer);
		}
	}
}
