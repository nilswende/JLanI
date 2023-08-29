package com.wn.nlp.jlani;

/**
 * Java Language Identifier.<br>
 * Identifies the most likely language of an unknown text.
 */
public interface JLanI {
	Response evaluate(Request request);
}
