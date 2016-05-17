package hello;

import java.util.Properties;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import edu.stanford.nlp.pipeline.StanfordCoreNLP;
@Configuration
public class NLPApplication {
	@Bean
	public StanfordCoreNLP stanfordCoreNLP() {
		Properties props = new Properties();
		props.setProperty("annotators","tokenize, ssplit, parse, sentiment, pos, lemma");
		return new StanfordCoreNLP(props);
	}

}
