package com.wn.nlp.jlani;

import com.wn.nlp.jlani.impl.SimpleWordList;
import com.wn.nlp.jlani.value.Language;
import net.jcip.annotations.Immutable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static com.wn.nlp.jlani.Preferences.Key.WORDLIST_DIR;

/**
 * Collection of available wordlists.
 */
@Immutable
public class WordLists {
	private static final Logger LOGGER = LoggerFactory.getLogger(WordLists.class);
	private final Map<Language, WordList> availableWordLists = new HashMap<>();
	
	/**
	 * Instances of this class are immutable if the supplied {@link WordList} instances are.
	 */
	public WordLists(final List<WordList> wordLists) {
		Objects.requireNonNull(wordLists);
		if (wordLists.isEmpty()) {
			throw new IllegalStateException("wordlists is empty");
		}
		for (final var wordList : wordLists) {
			Objects.requireNonNull(wordList);
			if (availableWordLists.containsKey(wordList.getLanguage())) {
				LOGGER.warn("Overwriting existing wordlist '{}'", wordList.getLanguage());
			}
			availableWordLists.put(wordList.getLanguage(), wordList);
		}
	}
	
	/**
	 * Creates wordlists from the configured directory.
	 */
	public static WordLists ofFiles() {
		var wordlistDirStr = Preferences.INSTANCE.get(WORDLIST_DIR);
		if (wordlistDirStr == null || wordlistDirStr.isBlank()) {
			throw new IllegalArgumentException("%s needs to be set in the config file".formatted(WORDLIST_DIR));
		}
		var wordlistDir = Path.of(wordlistDirStr);
		if (!Files.isDirectory(wordlistDir)) {
			throw new IllegalArgumentException("Missing directory: " + wordlistDir.toAbsolutePath());
		}
		try (var paths = Files.walk(wordlistDir)) {
			var wordLists = paths.filter(Files::isRegularFile).map(SimpleWordList::ofWordlistFile).toList();
			if (wordLists.isEmpty()) {
				throw new IllegalArgumentException("No wordlists provided in " + wordlistDir.toAbsolutePath());
			}
			return new WordLists(wordLists);
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
	}
	
	/**
	 * Returns the wordlists available for the requested languages.
	 *
	 * @param requestedLanguages the requested languages
	 */
	public Map<Language, WordList> getEvaluatedWordLists(final Set<Language> requestedLanguages) {
		Objects.requireNonNull(requestedLanguages);
		var availableLanguages = availableWordLists.keySet();
		if (!availableLanguages.containsAll(requestedLanguages)) {
			var unknown = new HashSet<>(requestedLanguages).removeAll(availableLanguages);
			throw new IllegalArgumentException("Unknown languages requested: " + unknown);
		}
		var evaluated = new HashMap<>(availableWordLists);
		if (!requestedLanguages.isEmpty()) {
			evaluated.keySet().retainAll(requestedLanguages);
		}
		return evaluated;
	}
}
