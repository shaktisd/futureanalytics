package org.demo.nlp;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations.SentencesAnnotation;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.LabeledScoredTreeFactory;
import edu.stanford.nlp.trees.PennTreeReader;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.trees.TreeFactory;
import edu.stanford.nlp.trees.TreeReader;
import edu.stanford.nlp.util.CoreMap;

public class CreateLearningData {

	private StanfordCoreNLP pipeline;

	public CreateLearningData() {
		Properties prop = new Properties();
		prop.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		// prop.setProperty("sentiment.model",
		// "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
		if (pipeline == null) {
			pipeline = new StanfordCoreNLP(prop);
		}
	}

	public static void main(String[] args) throws IOException {
		CreateLearningData cld = new CreateLearningData();
		List<LearningData> learningData = cld.process();
		StringBuilder sb = new StringBuilder();
		for(LearningData l : learningData){
			sb.append("@TAG_SENTENCE@" + l.getSentence()+"\n");
			for(String phrasePennTree : l.getPhrasesPennTrees()){
				sb.append(phrasePennTree+"\n");	
			}
			sb.append("\n");
		}
		System.out.println(sb);
	}

	public List<LearningData> process() throws IOException {
		List<Path> allFiles = readAllFiles("resources");
		List<LearningData> learningData = new ArrayList<LearningData>();
		for (Path path : allFiles) {
			String content = new String(Files.readAllBytes(path));
			LinkedHashMap<String, String> pennTreesMap = createPennTrees(content);
			for (Entry<String, String> entry : pennTreesMap.entrySet()) {
				LearningData ld = new LearningData();
				ld.setSentence(entry.getKey());
				ld.setSentencePennTree(entry.getValue());
				ld.setPhrasesPennTrees(createPhrasePennTrees(entry.getValue()));
				learningData.add(ld);
			}
		}
		return learningData;

	}

	public LinkedHashMap<String, String> createPennTrees(String content) {
		Annotation document = pipeline.process(content);
		List<CoreMap> sentences = document.get(SentencesAnnotation.class);
		LinkedHashMap<String, String> sentencePennTreeMap = new LinkedHashMap<String, String>();
		for (CoreMap sentence : sentences) {
			Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
			Tree copy = tree.deepCopy();
			setSentimentLabels(copy);
			sentencePennTreeMap.put(sentence.toString(), copy.toString());
		}
		return sentencePennTreeMap;
	}

	private void setSentimentLabels(Tree tree) {
		if (tree.isLeaf()) {
			return;
		}

		for (Tree child : tree.children()) {
			setSentimentLabels(child);
		}

		Label label = tree.label();
		if (!(label instanceof CoreLabel)) {
			throw new IllegalArgumentException(
					"Required a tree with CoreLabels");
		}
		CoreLabel cl = (CoreLabel) label;
		cl.setValue(Integer.toString(RNNCoreAnnotations.getPredictedClass(tree)));
	}

	public List<Path> readAllFiles(String folder) {
		List<Path> allFiles = new ArrayList<Path>();
		try {
			Files.walk(Paths.get(folder)).forEach(filePath -> {
				if (Files.isRegularFile(filePath)) {
					allFiles.add(filePath);
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return allFiles;
	}

	public List<String> createPhrasePennTrees(String sentence) {
		List<String> phrasePennTrees = new ArrayList<String>();
		TreeFactory tf = new LabeledScoredTreeFactory();
		StringReader reader = new StringReader(sentence);
		TreeReader tr = new PennTreeReader(reader, tf);
		Tree t = null;
		try {
			t = tr.readTree();
			tr.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		createSubTrees(t,phrasePennTrees);
		return phrasePennTrees;
	}

	public static void createSubTrees(Tree t,List<String> phrasePennTrees) {
		if (t.isLeaf())
			return;
		phrasePennTrees.add(t.toString());
		for (Tree subTree : t.children()) {
			createSubTrees(subTree,phrasePennTrees);
		}
	}

}
