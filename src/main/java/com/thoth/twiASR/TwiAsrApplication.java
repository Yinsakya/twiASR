package com.thoth.twiASR;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

@Controller
@SpringBootApplication
public class TwiAsrApplication {
	
	@RequestMapping("/transcribe")
	@ResponseBody
	String runRecognizer() throws IOException {
		
		String transcribed [] = new String [1];
		Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("am_t4");
        configuration.setDictionaryPath("thoth.dic");
        configuration.setLanguageModelPath("thoth.lm");

        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
	 	InputStream stream = new FileInputStream(new File("twi_test_1_refined.wav"));


       recognizer.startRecognition(stream);
       SpeechResult result;

       while ((result = recognizer.getResult()) != null) {
		    transcribed[0] = result.getHypothesis();
		    System.out.println(result.getWords());
        }

    	// Get individual words and their times.
		result = recognizer.getResult();
		recognizer.stopRecognition();
		System.out.println(transcribed[0]);
		return transcribed[0];
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TwiAsrApplication.class, args);
	}
}
