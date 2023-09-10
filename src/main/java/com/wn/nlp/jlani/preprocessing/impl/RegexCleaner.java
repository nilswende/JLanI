package com.wn.nlp.jlani.preprocessing.impl;

import com.wn.nlp.jlani.preprocessing.Cleaner;

import java.util.regex.Pattern;

/**
 * Removes unwanted characters from a string using regular expressions.
 */
public class RegexCleaner implements Cleaner {
	private final Pattern disallowed;
	
	private RegexCleaner(final String regex) {
		disallowed = Pattern.compile(regex);
	}
	
	/**
	 * Creates a new Cleaner from a regular expression.
	 *
	 * @param regex the regular expression
	 */
	public static Cleaner ofRegex(final String regex) {
		return regex == null || regex.isEmpty() ? new NullCleaner() : new RegexCleaner(regex);
	}
	
	@Override
	public String apply(String string) {
		return disallowed.matcher(string).replaceAll("");
	}
}
