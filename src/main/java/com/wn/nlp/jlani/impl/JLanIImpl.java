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
	private final Properties properties;
	private final WordLists wordLists;
	
	public JLanIImpl(final WordLists wordLists) {
		properties = initProperties();
		this.wordLists = Objects.requireNonNull(wordLists);
	}
	
	private Properties initProperties() {
		var properties = new Properties();
		if (Files.exists(PROPERTIES_PATH)) {
			try (var reader = new InputStreamReader(Files.newInputStream(PROPERTIES_PATH))) {
				properties.load(reader);
			} catch (IOException e) {
				throw new UncheckedIOException(e);
			}
		}
		return properties;
	}
	
	@Override
	public Response evaluate(final Request request) {
		Objects.requireNonNull(request);
		var evaluatedWordLists = wordLists.getEvaluatedWordLists(request.getLanguages());
		var sentence = preprocessSentence(request);
		
		var response = new Response(evaluatedWordLists.keySet(), sentence.size());
		
		evaluateScore(evaluatedWordLists, sentence, response);
		evaluateWords(evaluatedWordLists, sentence, response);
		
		return response;
	}
	
	private void evaluateScore(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		var evaluation = new Evaluation(evaluatedWordLists);
		for (final var word : sentence) {
			var evaluated = evaluation.evaluate(word);
			for (final var entry : evaluated.entrySet()) {
				var language = entry.getKey();
				var result = response.getResult(language);
				result.addScore(entry.getValue());
			}
		}
	}
	
	private void evaluateWords(final Map<Language, WordList> evaluatedWordLists, final List<Word> sentence, final Response response) {
		for (final var entry : evaluatedWordLists.entrySet()) {
			var language = entry.getKey();
			var wordList = entry.getValue();
			var result = response.getResult(language);
			for (final var word : sentence) {
				if (wordList.containsWord(word)) {
					result.addWord(word);
				}
			}
		}
	}
	
	private List<Word> preprocessSentence(final Request request) {
		var cleaner = RegexSentenceCleaner.ofProperty(properties);
		var blacklist = InMemoryBlacklist.ofProperty(properties);
		
		var sentence = request.getSentence();
		var cleanedSentence = cleaner.apply(sentence);
		var splitSentence = Arrays.asList(cleanedSentence.split(" "));
		var limitedSentence = request.limitWords(splitSentence.size()) ? sampleWords(splitSentence, request.getWordsToCheck()) : splitSentence;
		
		return limitedSentence.stream()
				.map(Word::new)
				.filter(word -> !blacklist.isBlacklisted(word))
				.toList();
	}
	
	private List<String> sampleWords(final List<String> splitSentence, final int wordsToCheck) {
		var step = splitSentence.size() / wordsToCheck;
		return IntStream.iterate(0, i -> i < wordsToCheck, i -> i + step)
				.mapToObj(splitSentence::get)
				.toList();
	}
}
