# JLanI 2
Reimplementation of the venerable language identification tool [JLanI](https://toolbox.wortschatz.uni-leipzig.de/toolbox/textclassification/jlani).
It identifies the most likely language of an unknown text.

Example:

````java
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.WordLists;
import com.wn.nlp.jlani.impl.InMemoryWordList;
import com.wn.nlp.jlani.impl.JLanIImpl;

import java.nio.file.Path;

public class Example {
	public static void main(String[] args) {
		var wordLists = createWordLists();
		var jLanI = new JLanIImpl(wordLists);
		
		var request = new Request("JLanI identifies the most likely language of an unknown text");
		var response = jLanI.evaluate(request);
		
		var language = response.getMostLikelyResult().getKey();
		System.out.println(language.name()); // prints 'en'
	}
	
	private static WordLists createWordLists() {
		var wordLists = new WordLists();
		wordLists.addWordList(InMemoryWordList.ofSerializedFile(Path.of("./resources/wordlists/de.txt")));
		wordLists.addWordList(InMemoryWordList.ofSerializedFile(Path.of("./resources/wordlists/en.txt")));
		return wordLists;
	}
}
````

Create a wordfile from your own reference corpus:

````java
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.WordFileCreator;

import java.nio.file.Path;

public class Example {
	public static void main(String[] args) {
		var text = """
				identifies the most likely language of an unknown text
				""";
		new WordFileCreator().create(new Request(text), Path.of("./new.txt"));
	}
}

````
