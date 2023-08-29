package com.wn.nlp.jlani;

import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.util.*;

/**
 * The response to a {@link Request} for language identification.
 */
public class Response {
	private final Map<Language, Result> results;
	private final int checkedWords;
	
	public Response(final Map<Language, Result> results, final int checkedWords) {
		if (checkedWords < 1) throw new IllegalArgumentException("No words checked");
		this.results = Objects.requireNonNull(results);
		this.checkedWords = checkedWords;
	}
	
	@Override
	public String toString() {
		return "Response{" +
			   "results=" + results +
			   ", checkedWords=" + checkedWords +
			   '}';
	}
	
	public Map.Entry<Language, Result> getMostLikelyResult() {
		return results.entrySet().stream()
				.max(Map.Entry.comparingByValue())
				.orElse(null);
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
	public static class Result implements Comparable<Result> {
		private double score = 1.0;
		private final List<Word> words = new ArrayList<>();
		private final int checkedWords;
		
		public Result(final int checkedWords) {
			if (checkedWords < 1) throw new IllegalArgumentException("No words checked");
			this.checkedWords = checkedWords;
		}
		
		public void addScore(final double score) {
			this.score *= score;
		}
		
		public void addWord(final Word word) {
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
				   ", checkedWords=" + checkedWords +
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
