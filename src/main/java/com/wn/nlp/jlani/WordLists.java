package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Collection of available wordlists.
 */
public class WordLists {
	private static final Logger LOGGER = LoggerFactory.getLogger(WordLists.class);
	private final Map<Language, WordList> availableWordLists = new HashMap<>();
	
	public WordLists() {
	}
	
	public WordLists(final List<WordList> wordLists) {
		wordLists.forEach(this::addWordList);
	}
	
	/**
	 * Adds a wordlist to the collection.
	 *
	 * @param wordList the wordlist
	 */
	public void addWordList(final WordList wordList) {
		Objects.requireNonNull(wordList);
		if (availableWordLists.containsKey(wordList.getLanguage())) {
			LOGGER.warn("Overwriting existing wordlist '{}'", wordList.getLanguage());
		}
		availableWordLists.put(wordList.getLanguage(), wordList);
	}
	
	/**
	 * Returns the wordlists available for the requested languages.
	 *
	 * @param requestedLanguages the requested languages
	 */
	public Map<Language, WordList> getEvaluatedWordLists(final Set<Language> requestedLanguages) {
		Objects.requireNonNull(requestedLanguages);
		if (availableWordLists.isEmpty()) {
			throw new IllegalStateException("No wordlists available");
		}
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