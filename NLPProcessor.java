package org.demo.nlp;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


public class NLPProcessor {
	public static void main(String args[]) throws IOException{
		NLP nlp = new NLP();
		String content = new String(Files.readAllBytes(Paths.get("input.txt")));
		nlp.findSentiment(content);
	}
}
