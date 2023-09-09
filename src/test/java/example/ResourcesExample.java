package example;

import com.wn.nlp.jlani.JLanI;
import com.wn.nlp.jlani.WordList;
import com.wn.nlp.jlani.WordLists;
import com.wn.nlp.jlani.impl.InMemoryWordList;
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
			return InMemoryWordList.ofWordCountFileReader(reader, Language.ofPath(path));
		}
	}
}
