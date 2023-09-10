package com.wn.nlp.jlani;

import org.junit.jupiter.api.Test;

import static com.wn.nlp.jlani.Preferences.Key.WORDLIST_DIR;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PreferencesTest {
	
	@Test
	void get() {
		var wordlistDirStr = Preferences.INSTANCE.get(WORDLIST_DIR);
		assertEquals("./resources/jlani/wordlists/", wordlistDirStr);
	}
	
	@Test
	void set() {
		var value = "dir";
		var originalWordlistDirStr = Preferences.INSTANCE.get(WORDLIST_DIR);
		Preferences.INSTANCE.set(WORDLIST_DIR, value);
		var wordlistDirStr = Preferences.INSTANCE.get(WORDLIST_DIR);
		assertEquals(value, wordlistDirStr);
		Preferences.INSTANCE.set(WORDLIST_DIR, originalWordlistDirStr);
	}
}