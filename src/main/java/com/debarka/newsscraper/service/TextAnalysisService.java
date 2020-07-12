package com.debarka.newsscraper.service;

import java.util.ArrayList;
import java.util.List;

import com.debarka.newsscraper.util.ArticleCacheUtil;
import com.debarka.newsscraper.util.NLPUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.util.Span;

@Service
public class TextAnalysisService {

    private static final Logger logger = LoggerFactory.getLogger(TextAnalysisService.class);
    private Integer progress;



    public String getTaskProgress(){
        return progress.toString() + " out of " + ArticleCacheUtil.articles.size();
    }

    public String runAnalysis(){
        NLPUtil nlpUtil =  new NLPUtil();
        progress = 0;
        SentenceDetectorME sdetector = nlpUtil.getSentenceDetector();
        NameFinderME nameFinderME = nlpUtil.getNameFinder();

        for(String article : ArticleCacheUtil.articles){
            String[] sentences = spitTextBySentences(article,sdetector);
            List<String> names = new ArrayList<>();
            for(String sentence : sentences){
                names.addAll(getAllPeopleNames(sentence, nameFinderME));
            }
            //List<String> names = getAllPeopleNames(article, nameFinderME);
            String logValue = "";
            logger.info("{}",names.size());
            for(String name : names){
                logValue = logValue + " " + name;
            }
            logger.info(logValue);
            progress++;
        }
        return "Analysis Successful";
    }

    private String[] spitTextBySentences(String text, SentenceDetectorME sdetector) {
	    String sentences[] = sdetector.sentDetect(text);
        return sentences;
    }
    
    private List<String> getAllPeopleNames(String text, NameFinderME nameFinderME){
        SimpleTokenizer tokenizer = SimpleTokenizer.INSTANCE;
        String[] tokens = tokenizer.tokenize(text);
        Span[] spans = nameFinderME.find(tokens);
        List<String> names = new ArrayList<>();
        for(Span span : spans){
            StringBuilder builder = new StringBuilder();
            for (int i = span.getStart(); i < span.getEnd(); i++) {
                builder.append(tokens[i]).append(" ");
            }
            String name = builder.toString();
            names.add(name);
        }
        nameFinderME.clearAdaptiveData();
        return names;
    }
}