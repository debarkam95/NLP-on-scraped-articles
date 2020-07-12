package com.debarka.newsscraper.service;

import java.io.IOException;
import java.time.Duration;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import com.debarka.newsscraper.util.ArticleCacheUtil;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;


@SuppressWarnings("all")
@Service
public class ArticleScrapeService {
   
    private static final Logger logger = LoggerFactory.getLogger(ArticleScrapeService.class);

    public Set<String> getArticlesFromUrl(String baseUrl) throws IOException{
        Document document = Jsoup.connect(baseUrl).get();
        Elements anchors = document.getElementsByClass("js-headline-text");
        for(Element element : anchors){
            //Pattern pattern = Pattern.compile("^(?!.*?(?:sport|cartoon|video|advertisement|membership|preference)).*$"); 
            String url = element.attr("href");
            //if(pattern.matcher(url).matches()){
                ArticleCacheUtil.articleUrlList.add(url);
                if(ArticleCacheUtil.articleUrlList.size() > 20)
                    return ArticleCacheUtil.articleUrlList;
            //}
            logger.info(url);
        }
        return ArticleCacheUtil.articleUrlList;

    }

	public Set<String> getArticleTexts() {
        Set<String> articleSet = new HashSet<>();
        ArticleCacheUtil.articleUrlList.forEach(articleUrl -> {
            String articleBody = fetchArticle(articleUrl);
            articleSet.add(articleBody == null?"":articleBody);
        });
        ArticleCacheUtil.articles = articleSet;
        return articleSet;
	}

    private String fetchArticle(String articleUrl){
        String articleBody = "";
        Document document = null;
        try {
            document = Jsoup.connect(articleUrl).get();
            Elements paragraphs = document.getElementsByTag("p");
        
            for(Element paragraph : paragraphs){
                articleBody = articleBody + " " + paragraph.text();
            }
        } catch (IOException | IllegalArgumentException e) {
            logger.error("{} {}",e,e.getMessage());
            return null;
        }
        return articleBody;
    }


}