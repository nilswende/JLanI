package com.wn.nlp.jlani;

import com.wn.nlp.jlani.util.IOUtil;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * JLanI preferences.
 */
public enum Preferences {
	INSTANCE;
	public static final String WORDLIST_DIR = "WordlistDir";
	public static final String BLACKLIST_FILE = "BlacklistFile";
	public static final String SPECIAL_CHARS = "SpecialChars";
	private final Properties properties;
	
	Preferences() {
		var path = Path.of("./config/jlani/lanikernel.ini");
		var properties = new Properties();
		if (Files.exists(path)) {
			try (var reader = IOUtil.newFileReader(path)) {
				properties.load(reader);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		this.properties = properties;
	}
	
	public String get(final String key) {
		return properties.getProperty(key);
	}
}
