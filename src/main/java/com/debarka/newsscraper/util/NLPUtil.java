package com.debarka.newsscraper.util;

import java.io.IOException;
import java.io.InputStream;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;

public class NLPUtil {

    public SentenceDetectorME getSentenceDetector(){
        InputStream is = getClass().getResourceAsStream("/models/en-sent.bin");
        SentenceModel model;
        try {
            model = new SentenceModel(is);
        } catch (IOException e) {
            return null;
        }
	    return new SentenceDetectorME(model);
    }

    public NameFinderME getNameFinder(){
        InputStream inputStreamNameFinder = getClass().getResourceAsStream("/models/en-ner-person.bin");
        TokenNameFinderModel model;
        try {
            model = new TokenNameFinderModel(inputStreamNameFinder);
        } catch (IOException e) {
            return null;
        }
        return new NameFinderME(model);
    }
    
}