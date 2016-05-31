package org.demo.nlp;

import java.util.ArrayList;
import java.util.List;

public class LearningData {
	private String sentence;
	private String sentencePennTree;
	private List<String> phrasesPennTrees = new ArrayList<String>();
	public String getSentence() {
		return sentence;
	}
	public void setSentence(String sentence) {
		this.sentence = sentence;
	}
	public String getSentencePennTree() {
		return sentencePennTree;
	}
	public void setSentencePennTree(String sentencePennTree) {
		this.sentencePennTree = sentencePennTree;
	}
	public List<String> getPhrasesPennTrees() {
		return phrasesPennTrees;
	}
	public void setPhrasesPennTrees(List<String> phrasesPennTrees) {
		this.phrasesPennTrees = phrasesPennTrees;
	}
	@Override
	public String toString() {
		return "LearningData [sentence=" + sentence + ", sentencePennTree="
				+ sentencePennTree + ", phrasesPennTrees=" + phrasesPennTrees
				+ "]\n";
	}
	
	
	
}
