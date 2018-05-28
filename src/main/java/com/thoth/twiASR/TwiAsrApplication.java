package com.thoth.twiASR;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import edu.cmu.sphinx.api.Configuration;
import edu.cmu.sphinx.api.LiveSpeechRecognizer;
import edu.cmu.sphinx.api.SpeechResult;
import edu.cmu.sphinx.api.StreamSpeechRecognizer;
import edu.cmu.sphinx.result.WordResult;

@Controller
@SpringBootApplication
public class TwiAsrApplication {
	
	@RequestMapping(value = "/transcribe", method = RequestMethod.POST)
	@ResponseBody
	String runRecognizer(@RequestParam("file") MultipartFile audio) throws IOException {
		
		String transcribed [] = new String [1];
		byte[] audiofile = null;
		audiofile = audio.getBytes();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        long uniqueTime = timestamp.getTime();
        String ID = Long.toString(uniqueTime);
        String filename = "audio_" + ID + ".wav";
        try {
			BufferedOutputStream ostream =
	                new BufferedOutputStream(new FileOutputStream(new File(filename)));
			ostream.write(audiofile);
			ostream.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		Configuration configuration = new Configuration();

        configuration.setAcousticModelPath("am_t4");
        configuration.setDictionaryPath("thoth.dic");
        configuration.setLanguageModelPath("thoth.lm");

        configuration.setSampleRate(16000);
        
        StreamSpeechRecognizer recognizer = new StreamSpeechRecognizer(configuration);
	 	InputStream stream = new FileInputStream(new File(filename));

       recognizer.startRecognition(stream);
       SpeechResult result;

       while ((result = recognizer.getResult()) != null) {
		    transcribed[0] = result.getHypothesis();
		    System.out.println(result.getWords());
        }

    	// Get individual words and their times.
		result = recognizer.getResult();
		recognizer.stopRecognition();
		File file = new File(filename);
		if (file.exists()) {
			file.delete();
		}
		System.out.println(transcribed[0]);
		return transcribed[0];
	}
	
	public static void main(String[] args) {
		SpringApplication.run(TwiAsrApplication.class, args);
	}
}
