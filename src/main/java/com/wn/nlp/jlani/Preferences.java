package com.wn.nlp.jlani;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * JLanI preferences.
 */
public enum Preferences {
	INSTANCE;
	public static final String SPECIAL_CHARS = "SpecialChars";
	public static final String BLACKLIST_FILE = "BlacklistFile";
	private final Properties properties;
	
	Preferences() {
		var path = Path.of("./config/jlani/lanikernel.ini");
		var properties = new Properties();
		if (Files.exists(path)) {
			try (var reader = new InputStreamReader(Files.newInputStream(path))) {
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
