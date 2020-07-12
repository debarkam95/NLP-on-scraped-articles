package com.debarka.newsscraper.api;

import java.io.IOException;
import java.time.Instant;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import javax.servlet.http.HttpServletResponse;

import com.debarka.newsscraper.service.ArticleScrapeService;
import com.debarka.newsscraper.service.TextAnalysisService;
import com.debarka.newsscraper.util.ArticleCacheUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

@SuppressWarnings("all")
@RestController
public class ScrapeController {

    private static final Logger logger = LoggerFactory.getLogger(ScrapeController.class);
    
    @Autowired
    private ArticleScrapeService articleScrapeService;

    @Autowired
    private TextAnalysisService textAnalysisService;

    @GetMapping("/articles")
    public ResponseEntity<Set<String>> getAllArticles(@RequestParam("baseUrl") String baseUrl) throws IOException{
        logger.info("Now fetching data from {}", baseUrl);
        return ResponseEntity.ok().body(articleScrapeService.getArticlesFromUrl(baseUrl));
    }

    @GetMapping("/articleText")
    public ResponseEntity<Set<String>> getTextFromAllArticles(){
        return ResponseEntity.ok().body(articleScrapeService.getArticleTexts());
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> startAnalysis(){
        CompletableFuture.supplyAsync(() -> textAnalysisService.runAnalysis());
        return ResponseEntity.ok().body("Task has been submitted and will be completed in the background");
    }

    @GetMapping("/status")
    public ResponseEntity<String> checkTaskStatus(){
        return ResponseEntity.ok().body(textAnalysisService.getTaskProgress());
    }

    
    @GetMapping("/heartbeat")
    public ResponseEntity<String> test(){
        return ResponseEntity.ok().body(Instant.now().toString());
    }


}