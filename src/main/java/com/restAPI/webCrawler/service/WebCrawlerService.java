package com.restAPI.webCrawler.service;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.Gson;

@Component
public class WebCrawlerService {
	
	@Autowired
	public Gson gsonObj;
	
	public String startCrawling(String completeDomainURL, Integer maxPageCrawlLimit) throws InterruptedException, ExecutionException
	{
		FetchAllLinks fetchAllLinks = new FetchAllLinks(completeDomainURL,maxPageCrawlLimit);
		List<String> webLinkList = new ArrayList<String>();
		webLinkList.add(completeDomainURL);
		fetchAllLinks.fetchDetails(webLinkList);
		return gsonObj.toJson(fetchAllLinks.visitedLink);
	}

}
