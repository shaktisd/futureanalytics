package hello;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;


@Controller
public class WebController extends WebMvcConfigurerAdapter {
	
	@Autowired SentimentService sentimentService;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/results").setViewName("results");
    }

    @RequestMapping(value="/", method=RequestMethod.GET)
    public String showForm(PersonForm personForm) {
        return "form";
    }

    @RequestMapping(value="/", method=RequestMethod.POST)
    public String checkPersonInfo(@Valid PersonForm personForm, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "form";
        }

        return "redirect:/results";
    }
    
    @RequestMapping(value="/runmodel", method=RequestMethod.GET)
    public String runModel(@Valid PersonForm personForm, BindingResult bindingResult, ArrayList<NLPSentimentResult> calculatedSentiment) {
    	calculatedSentiment = sentimentService.calculateSentiment(personForm.getName());
    	System.out.println("calculatedSentiment " + calculatedSentiment);
        if (bindingResult.hasErrors()) {
            return "form";
        }

        return "redirect:/results";
    }
}
