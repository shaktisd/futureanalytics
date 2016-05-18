package org.demo.nlp;
import java.util.Properties;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

public class NLP {
	StanfordCoreNLP pipeline;

	public NLP() {
		//pipelineProps.setProperty("sentiment.model", sentimentModel);
		Properties prop = new Properties();
		prop.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		prop.setProperty("sentiment.model", "edu/stanford/nlp/models/sentiment/sentiment.ser.gz");
		if(pipeline == null){
			pipeline = new StanfordCoreNLP(prop);	
		}
	}

	public int findSentiment(String text) {
		int mainSentiment = 0;
		if (text != null && text.length() > 0) {
			Annotation annotation = pipeline.process(text);
			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
				Tree copy = tree.deepCopy();
				setSentimentLabels(copy);
				String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
				System.out.println(sentence);
				System.out.println(sentiment);
				System.out.println(copy);
				
			}
		}
		return mainSentiment;
	}
	
	
	/**
	   * Sets the labels on the tree (except the leaves) to be the integer
	   * value of the sentiment prediction.  Makes it easy to print out
	   * with Tree.toString()
	   */
	  private void setSentimentLabels(Tree tree) {
	    if (tree.isLeaf()) {
	      return;
	    }

	    for (Tree child : tree.children()) {
	      setSentimentLabels(child);
	    }

	    Label label = tree.label();
	    if (!(label instanceof CoreLabel)) {
	      throw new IllegalArgumentException("Required a tree with CoreLabels");
	    }
	    CoreLabel cl = (CoreLabel) label;
	    cl.setValue(Integer.toString(RNNCoreAnnotations.getPredictedClass(tree)));
	  }
}