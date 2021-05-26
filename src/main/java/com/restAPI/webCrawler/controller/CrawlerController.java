package com.restAPI.webCrawler.controller;

import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.restAPI.webCrawler.service.WebCrawlerService;

@RestController
public class CrawlerController {
	
	@Autowired
	public WebCrawlerService webCrawlerService;
	
	@GetMapping(value = "{domainLink}/crawl")
    public  String crawlDomain(@PathVariable String domainLink, @RequestParam(defaultValue = "200") Integer limit) throws InterruptedException, ExecutionException{
        String CompleteDomainURL = "https://".concat(domainLink);
		return webCrawlerService.startCrawling(CompleteDomainURL, limit);
    }
	
}
