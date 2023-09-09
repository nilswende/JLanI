package com.wn.nlp.jlani.preprocessing;

import java.util.regex.Pattern;

/**
 * Removes unwanted characters from a sentence using regular expressions.
 */
public class RegexSentenceCleaner implements SentenceCleaner {
	private final Pattern disallowed;
	
	private RegexSentenceCleaner(final String regex) {
		disallowed = Pattern.compile(regex);
	}
	
	/**
	 * Creates a new SentenceCleaner from a regular expression.
	 *
	 * @param regex the regular expression
	 */
	public static SentenceCleaner ofRegex(final String regex) {
		return regex == null || regex.isEmpty() ? new NullSentenceCleaner() : new RegexSentenceCleaner(regex);
	}
	
	@Override
	public String apply(CharSequence sentence) {
		return disallowed.matcher(sentence).replaceAll("");
	}
}
