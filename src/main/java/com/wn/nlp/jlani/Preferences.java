package com.wn.nlp.jlani;

import com.wn.nlp.jlani.util.IOUtil;
import net.jcip.annotations.GuardedBy;
import net.jcip.annotations.ThreadSafe;

import java.io.IOException;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/**
 * JLanI preferences.
 */
@ThreadSafe
public enum Preferences {
	/**
	 * The singleton instance.
	 */
	INSTANCE;
	@GuardedBy("itself")
	private final Properties properties;
	
	Preferences() {
		var path = Path.of("./config/jlani/lanikernel.ini");
		var properties = new Properties();
		try (var reader = readFile(path)) {
			if (reader != null) {
				properties.load(reader);
			}
		} catch (IOException e) {
			throw new UncheckedIOException(e);
		}
		this.properties = properties;
	}
	
	private static Reader readFile(final Path path) throws IOException {
		if (Files.exists(path)) {
			return IOUtil.newFileReader(path);
		} else if (IOUtil.resourceExists(path)) {
			return IOUtil.newResourceReader(path);
		}
		return null;
	}
	
	public String get(final Key key) {
		return properties.getProperty(key.getName());
	}
	
	public void set(final Key key, final String value) {
		properties.setProperty(key.getName(), value);
	}
	
	/**
	 * The preference keys.
	 */
	public enum Key {
		/**
		 * Path to the wordlist directory.
		 */
		WORDLIST_DIR("WordlistDir"),
		/**
		 * Path to the blacklist file.
		 */
		BLACKLIST_FILE("BlacklistFile"),
		/**
		 * Regex used to remove special characters from input text.
		 */
		SPECIAL_CHARS("SpecialChars"),
		;
		private final String name;
		
		Key(final String name) {
			this.name = name;
		}
		
		public String getName() {
			return name;
		}
	}
}
