package com.wn.nlp.jlani.util;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public final class IOUtil {
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	
	private IOUtil() {
	}
	
	public static PrintWriter newFileWriter(final Path destination, final boolean gzip) throws IOException {
		var dst = destination;
		var fileName = dst.getFileName().toString();
		if (gzip && !fileName.endsWith(".gz")) {
			dst = Path.of(dst + ".gz");
		}
		var out = Files.newOutputStream(dst);
		return new PrintWriter(
				gzip ? newGZIPOutputStream(out) : new BufferedOutputStream(out)
		);
	}
	
	public static GZIPOutputStream newGZIPOutputStream(final OutputStream out) throws IOException {
		return new GZIPOutputStream(out, DEFAULT_BUFFER_SIZE);
	}
	
	public static Reader newFileReader(final Path path) throws IOException {
		var fileName = path.getFileName().toString();
		return newReader(Files.newInputStream(path), fileName.endsWith(".gz"));
	}
	
	public static Reader newResourceReader(final Path path) throws IOException {
		var fileName = path.getFileName().toString();
		var in = Objects.requireNonNull(IOUtil.class.getClassLoader().getResourceAsStream(path.toString()));
		return newReader(in, fileName.endsWith(".gz"));
	}
	
	public static InputStreamReader newReader(final InputStream in, final boolean gunzip) throws IOException {
		return new InputStreamReader(
				gunzip ? newGZIPInputStream(in) : new BufferedInputStream(in)
		);
	}
	
	public static GZIPInputStream newGZIPInputStream(final InputStream in) throws IOException {
		return new GZIPInputStream(in, DEFAULT_BUFFER_SIZE);
	}
}
