package org.demo.nlp;
import java.util.Properties;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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
				StringBuilder sb = new StringBuilder();
				toStringBuilder(sb,false,copy);
				System.out.println("TREE IS \n");
				System.out.println(sb);
				Node root = new Node();
				createTreeForJson(root,copy);
				try {
					ObjectMapper mapper = new ObjectMapper();
					mapper.enable(SerializationFeature.INDENT_OUTPUT);
					String writeValueAsString =mapper.writeValueAsString(root);
					System.out.println("JSON\n" + writeValueAsString);
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
		}
		return mainSentiment;
	}
	
	public Node createTreeForJson(Node node, Tree tree){
		if(tree.isLeaf()){
			if(tree.label() != null){
				node.setValue(tree.label().value());	
			}
		}else {
			if (tree.value() != null && tree.label() != null) {
				node.setValue(tree.label().value());
			}
			Tree[] children = tree.children();
			if(children != null){
				for(Tree child : children){
					Node childNode = new Node();
					node.getChildren().add(childNode);
					createTreeForJson(childNode,child);
				}
			}
		}
		return node;
	}
	
	public StringBuilder toStringBuilder(StringBuilder sb, boolean printOnlyLabelValue, Tree tree) {
	    if (tree.isLeaf()) {
	      if (tree.label() != null) {
	        if(printOnlyLabelValue) {
	          sb.append(tree.label().value());
	        } else {
	          sb.append(tree.label());
	        }
	      }
	      return sb;
	    } else {
	      sb.append('(');
	      if (tree.label() != null) {
	        if (printOnlyLabelValue) {
	          if (tree.value() != null) {
	            sb.append(tree.label().value());
	          }
	          // don't print a null, just nothing!
	        } else {
	          sb.append(tree.label());
	        }
	      }
	      Tree[] kids = tree.children();
	      if (kids != null) {
	        for (Tree kid : kids) {
	          sb.append(' ');
	          kid.toStringBuilder(sb, printOnlyLabelValue);
	        }
	      }
	      return sb.append(')');
	    }
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