package com.wn.nlp.jlani.impl;

import com.wn.nlp.jlani.JLanI;
import com.wn.nlp.jlani.Request;
import com.wn.nlp.jlani.Response;
import com.wn.nlp.jlani.WordLists;
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
		var scores = new HashMap<Language, Double>();
		evaluatedWordLists.keySet().forEach(l -> scores.put(l, 1.0));
		var words = new HashMap<Language, List<Word>>();
		evaluatedWordLists.keySet().forEach(l -> words.put(l, new ArrayList<>()));
		for (final var word : sentence) {
			var evaluated = evaluation.evaluate(word);
			scores.replaceAll((l, d) -> d * evaluated.get(l));
			evaluatedWordLists.entrySet().stream()
					.filter(e -> e.getValue().getLikelihood(word) != null)
					.forEach(e -> words.get(e.getKey()).add(word));
		}
		var results = new HashMap<Language, Response.Result>();
		evaluatedWordLists.keySet().forEach(l -> results.put(l, new Response.Result(scores.get(l), words.get(l), sentence.size())));
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
