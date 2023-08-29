package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.WordList;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Evaluates words based on a given set of wordlists.
 */
class Evaluation {
	private static final double REMAINDER_BASE = 0.1;
	private final Map<Language, WordList> wordLists;
	
	public Evaluation(final Map<Language, WordList> wordLists) {
		this.wordLists = wordLists;
	}
	
	/**
	 * Evaluates a word.
	 *
	 * @param word the word
	 * @return the evaluated languages and the likelihood of the word belonging to each
	 */
	public Map<Language, Double> evaluate(final Word word) {
		var map = createMap(word);
		replaceMissingValues(map);
		normalizeValues(map);
		return map;
	}
	
	private Map<Language, Double> createMap(final Word word) {
		var map = new HashMap<Language, Double>(); // allows null values
		wordLists.forEach((l, wl) -> map.put(l, wl.getLikelihood(word)));
		return map;
	}
	
	private void replaceMissingValues(final Map<Language, Double> map) {
		var missing = map.values().stream()
				.filter(Objects::isNull)
				.count();
		if (missing > 0) {
			var remainder = missing == map.size() ? 1.0 : (REMAINDER_BASE / map.size()) * getSum(map);
			map.replaceAll((l, d) -> d == null ? remainder : d);
		}
	}
	
	private void normalizeValues(final Map<Language, Double> map) {
		var sum = getSum(map);
		map.replaceAll((l, d) -> d * map.size() / sum);
	}
	
	private double getSum(final Map<Language, Double> map) {
		return map.values().stream()
				.filter(Objects::nonNull)
				.mapToDouble(d -> d)
				.sum();
	}
}
