package hello;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ling.Label;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;

@Service
public class SentimentService {

@Autowired StanfordCoreNLP stanfordCoreNLP;

public ArrayList<NLPSentimentResult> calculateSentiment(String text){
	ArrayList<NLPSentimentResult> result = new ArrayList<NLPSentimentResult>();
	if (text != null && text.length() > 0) {
		Annotation annotation = stanfordCoreNLP.process(text);
		for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
			NLPSentimentResult row = new NLPSentimentResult();
			Tree tree = sentence.get(SentimentCoreAnnotations.AnnotatedTree.class);
			Tree copy = tree.deepCopy();
			setSentimentLabels(copy);
			String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
			row.setSentence(sentence.toString());
			row.setSentiment(sentiment);
			result.add(row);
		}
	}
	return result;
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
	      throw new IllegalArgumentException("Required a tree with CoreLabels");
	    }
	    CoreLabel cl = (CoreLabel) label;
	    cl.setValue(Integer.toString(RNNCoreAnnotations.getPredictedClass(tree)));
	  }



}
