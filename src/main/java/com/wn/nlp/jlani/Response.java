package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.*;

/**
 * The response to a {@link Request} for language identification.
 */
public class Response {
	private final int checkedWords;
	private final Map<Language, Result> results = new HashMap<>();
	
	public Response(final Set<Language> languages, final int checkedWords) {
		this(checkedWords);
		Objects.requireNonNull(languages).forEach(this::createResult);
	}
	
	public Response(final int checkedWords) {
		if (checkedWords < 1) throw new IllegalArgumentException("No words checked");
		this.checkedWords = checkedWords;
	}
	
	Result createResult(final Language language) {
		var result = new Result();
		results.put(language, result);
		return result;
	}
	
	@Override
	public String toString() {
		return "Response{" +
			   "checkedWords=" + checkedWords +
			   ", results=" + results +
			   '}';
	}
	
	/**
	 * Returns the most likely Language and Result, i.e. with the highest score.
	 */
	public Map.Entry<Language, Result> getMostLikelyResult() {
		return results.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.orElse(null);
	}
	
	public Result getResult(final Language language) {
		return results.get(language);
	}
	
	public Map<Language, Result> getResults() {
		return results;
	}
	
	public int getCheckedWords() {
		return checkedWords;
	}
	
	/**
	 * The result of a language identification for a specific language.
	 */
	public class Result implements Comparable<Result> {
		private double score = 1.0;
		private final List<Word> words = new ArrayList<>();
		
		void addScore(final double score) {
			this.score *= score;
		}
		
		void addWord(final Word word) {
			words.add(word);
		}
		
		@Override
		public int compareTo(final Result o) {
			return Comparator.comparingDouble(Result::getScore).compare(this, o);
		}
		
		@Override
		public String toString() {
			return "Result{" +
				   "score=" + score +
				   ", words=" + words +
				   ", coverage=" + getCoverage() +
				   '}';
		}
		
		/**
		 * The likelihood of the language.
		 */
		public double getScore() {
			return score;
		}
		
		/**
		 * All checked words from the input sentence that likely belong to the language.
		 */
		public List<Word> getWords() {
			return words;
		}
		
		/**
		 * The percentage of checked words the language likely covers.
		 */
		public double getCoverage() {
			return (double) words.size() / checkedWords;
		}
	}
}
