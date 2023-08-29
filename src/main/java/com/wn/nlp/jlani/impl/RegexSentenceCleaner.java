package com.wn.nlp.jlani.impl;

import java.util.Objects;
import java.util.Properties;
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
	 * @param properties the JLanI properties. May contain a regular expression
	 */
	public static SentenceCleaner ofProperty(final Properties properties) {
		Objects.requireNonNull(properties);
		var regex = properties.getProperty("SpecialChars");
		return ofRegex(regex);
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
