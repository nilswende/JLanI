package example;

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
