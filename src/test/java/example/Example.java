package example;

import com.wn.nlp.jlani.JLanI;
import com.wn.nlp.jlani.Request;

class Example {
	public static void main(String[] args) {
		var jLanI = new JLanI();
		
		var request = new Request("JLanI identifies the most likely language of an unknown text");
		var response = jLanI.evaluate(request);
		
		var language = response.getMostLikelyResult().getKey();
		System.out.println(language.name()); // prints 'en'
	}
}
