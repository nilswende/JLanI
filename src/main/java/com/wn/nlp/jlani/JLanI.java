package com.wn.nlp.jlani;

/**
 * Java Language Identifier.<br>
 * Identifies the most likely language of an unknown text.
 */
public interface JLanI {
	void addWordList(WordList wordList);
	Response evaluate(Request request);
}
