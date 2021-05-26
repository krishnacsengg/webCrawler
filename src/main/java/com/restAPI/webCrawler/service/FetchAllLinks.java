package com.restAPI.webCrawler.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.restAPI.webCrawler.entities.ImgDetails;
import com.restAPI.webCrawler.entities.UrlDetails;

public class FetchAllLinks {

	public ConcurrentHashMap<String, UrlDetails> visitedLink = new ConcurrentHashMap<>();
	public LinkedBlockingQueue<String> urlLinkNotVisited = new LinkedBlockingQueue<>();
	ExecutorService downloadExecutor = Executors.newFixedThreadPool(2);
	AtomicInteger counter = new AtomicInteger(0);
	CopyOnWriteArrayList<Future<ConcurrentHashMap<String, UrlDetails>>> futures = new CopyOnWriteArrayList<>();
	public String DOMAIN_NAME;
	public Integer maxPageCrawlLimit;

	public FetchAllLinks(String mainDomainLink, Integer maxPageCrawlLimit) {
		this.DOMAIN_NAME = mainDomainLink;
		this.maxPageCrawlLimit = maxPageCrawlLimit;
	}

	public void fetchDetails(List<String> webLink) throws InterruptedException, ExecutionException {

		urlLinkNotVisited.addAll(webLink);

		while (urlLinkNotVisited.size() > 0) {

			String url = urlLinkNotVisited.remove();

			if (visitedLink.containsKey(url)) {
				continue;
			} else {
				counter.getAndIncrement();
				visitedLink.put(url, new UrlDetails(null, null, "Started", null));
			}

			futures.add(downloadExecutor.submit(new Downloader(url)));
		}

		for (Future<ConcurrentHashMap<String, UrlDetails>> future : futures) {
			// future.get();
			List<String> domainLinks = future.get().values().stream().findFirst().get().getDomainLink().stream()
					.distinct().collect(Collectors.toList());

			visitedLink.putAll(future.get());

			domainLinks.removeIf(x -> visitedLink.containsKey(x));

			if (domainLinks.size() > 0) {
				int crawlerCountPending = maxPageCrawlLimit - counter.get();
				if (crawlerCountPending > 0) {
					domainLinks = domainLinks.stream().limit(crawlerCountPending).collect(Collectors.toList());
					fetchDetails(domainLinks);
				}
			}

		}

		// System.out.println("Completed1");

		downloadExecutor.shutdown();

	}

	public class Downloader implements Callable<ConcurrentHashMap<String, UrlDetails>> {
		private ConcurrentHashMap<String, UrlDetails> visitedLink = new ConcurrentHashMap<>();
		private String url;

		public Downloader(String url) {
			this.url = url;
		}

		public ConcurrentHashMap<String, UrlDetails> call() {

			List<String> domainLink = new ArrayList<>();
			List<String> externalLink = new ArrayList<>();
			List<ImgDetails> imgDetails = new ArrayList<ImgDetails>();
			String linkValue;
			String imgDesc;
			Document doc = null;
			try {
				doc = Jsoup.connect(url).get();
			} catch (IOException e) {

				String responseDetails = "Link Not Working";
				visitedLink.put(url, new UrlDetails(domainLink, externalLink, responseDetails, imgDetails));
				return visitedLink;
			}
			Elements imgs = doc.getElementsByTag("img");
			Elements links = doc.select("a[href]");
			for (Element link : links) {

				linkValue = link.attr("href");
				if (linkValue.startsWith("http")) {
					externalLink.add(linkValue);
				} else if (linkValue.equals("/")) {
					domainLink.add(DOMAIN_NAME);
				} else if (linkValue.startsWith("/"))
					domainLink.add(DOMAIN_NAME + linkValue);

			}

			for (Element img : imgs) {

				linkValue = img.attr("src");
				imgDesc = img.attr("alt");
				imgDetails.add(new ImgDetails(linkValue, imgDesc));

			}

			String responseDetails = "Working";

			visitedLink.put(url, new UrlDetails(domainLink, externalLink, responseDetails, imgDetails));

			System.out.println("Crawler task completed for link: " + url);
			return visitedLink;

		}
	}

}

