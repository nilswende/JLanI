# JLanI 2

Reimplementation of the venerable language identification
tool [JLanI](https://toolbox.wortschatz.uni-leipzig.de/toolbox/textclassification/jlani).
It identifies the most likely language of an unknown text.

JLanI by default uses the wordlist files contained in ``./resources/jlani/wordlists``.
The included languages are Chinese, English, French, German, Korean, Japanese, Mandalorian and Spanish.

Example:

````java
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.JLanI;

class Example {
	public static void main(String[] args) {
		var jLanI = new JLanI();
		
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
			new WordListCreator().createFromText(text, writer);
		}
	}
}
````

and drop it in ``./resources/jlani/wordlists`` to be available on startup.

A good starting point for corpora is the [Leipzig Corpora Collection](https://wortschatz.uni-leipzig.de/en/download).
Use ``WortschatzWordListCreator`` to create a wordlist from one of their word files.

## Using resources

If you don't want to use actual files, you can for example deliver your wordlist files as resources in your JAR file.
Simply read the wordlists and pass them to the JLanI constructor:

````java
import com.wn.nlp.jlani.JLanI;
import com.wn.nlp.jlani.WordList;
import com.wn.nlp.jlani.WordLists;
import com.wn.nlp.jlani.impl.SimpleWordList;
import com.wn.nlp.jlani.util.IOUtil;
import com.wn.nlp.jlani.value.Language;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

class ResourcesExample {
	public static void main(String[] args) throws IOException {
		var de = wordListOfResource("de.txt.gz");
		var en = wordListOfResource("en.txt.gz");
		var wordLists = new WordLists(List.of(de, en));
		
		var jLanI = new JLanI(wordLists);
	}
	
	private static WordList wordListOfResource(final String name) throws IOException {
		var path = Path.of(name);
		try (var reader = IOUtil.newResourceReader(path)) {
			return SimpleWordList.ofWordlistFileReader(reader, Language.ofPath(path));
		}
	}
}
````

## Configuration

There are three ways to configure JLanI:

- Edit the resources file ``src/main/resources/config/jlani/lanikernel.ini``.
- Create the external file ``./config/jlani/lanikernel.ini``. If it exists, it takes precedence over the resources file.
- Set the configuration programmatically via the ``Preferences`` class.

Any changes are applied only to new ``JLanI`` instances.

## Changes from the original

- Preprocessing:
  - Splits words on any whitespace of any length
  - Performs [Unicode normalization](https://docs.oracle.com/javase/tutorial/i18n/text/normalizerapi.html) to NFKC
  - Removes numbers
  - Removes empty strings
  - Samples from the whole input text instead of sampling the first ``n`` words
