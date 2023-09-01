# JLanI 2
Reimplementation of the venerable language identification tool [JLanI](https://toolbox.wortschatz.uni-leipzig.de/toolbox/textclassification/jlani).
It identifies the most likely language of an unknown text.

The languages available by default are German, English and French.

Example:

````java
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.impl.JLanIImpl;

class Example {
	public static void main(String[] args) {
		var jLanI = new JLanIImpl();
		
		var request = new Request("JLanI identifies the most likely language of an unknown text");
		var response = jLanI.evaluate(request);
		
		var language = response.getMostLikelyResult().getKey();
		System.out.println(language.name()); // prints 'en'
	}
}
````

## Other languages

Create a wordlist from your own reference corpus:

````java
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
````

and drop it in ``resources/jlani/wordlists`` to be available on startup.
