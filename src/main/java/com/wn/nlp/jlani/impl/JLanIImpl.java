package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.*;
import com.wn.nlp.jlani.value.Language;
import com.wn.nlp.jlani.value.Word;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.IntStream;

/**
 * The default JLanI implementation.
 */
public class JLanIImpl implements JLanI {
	private static final Path PROPERTIES_PATH = Path.of("./config/jlani/lanikernel.ini");
	private final Properties properties = new Properties();
	private final WordLists wordLists;
	
	public JLanIImpl(final WordLists wordLists) {
		initProperties();
		this.wordLists = wordLists;
	}
	
	private void initProperties() {
		if (Files.exists(PROPERTIES_PATH)) {
			try (var reader = new InputStreamReader(Files.newInputStream(PROPERTIES_PATH))) {
				properties.load(reader);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
	}
	
	@Override
	public Response evaluate(final Request request) {
		Objects.requireNonNull(request);
		var evaluatedWordLists = wordLists.getEvaluatedWordLists(request.getLanguages());
		var evaluation = new Evaluation(evaluatedWordLists);
		var sentence = preprocessSentence(request);
		
		var results = new HashMap<Language, Response.Result>();
		evaluatedWordLists.keySet().forEach(l -> results.put(l, new Response.Result(sentence.size())));
		
		for (final var word : sentence) {
			var evaluated = evaluation.evaluate(word);
			for (final var entry : evaluated.entrySet()) {
				var language = entry.getKey();
				var result = results.get(language);
				result.addScore(entry.getValue());
				if (evaluatedWordLists.get(language).containsWord(word)) {
					result.addWord(word);
				}
				
			}
		}
		
		return new Response(results, sentence.size());
	}
	
	private List<Word> preprocessSentence(final Request request) {
		var sentence = request.getSentence();
		var cleanedSentence = RegexSentenceCleaner.ofProperty(properties).apply(sentence);
		var splitSentence = Arrays.asList(cleanedSentence.split(" "));
		if (request.limitWords(splitSentence.size())) {
			splitSentence = sampleWords(splitSentence, request.getWordsToCheck());
		}
		var blacklist = InMemoryBlacklist.ofProperty(properties);
		return splitSentence.stream()
				.map(Word::new)
				.filter(word -> !blacklist.isBlacklisted(word))
				.toList();
	}
	
	private List<String> sampleWords(final List<String> splitSentence, final int wordsToCheck) {
		var stepping = splitSentence.size() / wordsToCheck;
		return IntStream.iterate(0, i -> i < splitSentence.size(), i -> i + stepping)
				.mapToObj(splitSentence::get)
				.toList();
	}
}
