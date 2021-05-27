package com.restAPI.webCrawler;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static java.nio.charset.Charset.defaultCharset;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.google.common.io.Resources;
import com.restAPI.webCrawler.entities.UrlDetails;
import com.restAPI.webCrawler.service.FetchAllLinks;

@SpringBootTest
class WebCrawlerApplicationTests {

	@Test
	void contextLoads() throws InterruptedException, ExecutionException, IOException {

		WireMockServer wireMockServer = new WireMockServer(8080);

		wireMockServer.start();

		try {
			stubFor(get(urlMatching("/output.html")).willReturn(aResponse().withStatus(200)
					.withBody(Resources.toString(Resources.getResource("output.html"), defaultCharset()))
					.withHeader("Content-Type", "text/html")));
		} catch (IOException e) {
			e.printStackTrace();
		}

		FetchAllLinks fetchAllLinks = new FetchAllLinks("http://localhost:8080", 20);
		List<String> webLinkList = new ArrayList<>();
		webLinkList.add("http://localhost:8080/output.html");
		fetchAllLinks.fetchDetails(webLinkList);
		UrlDetails urldetails = fetchAllLinks.visitedLink.get("http://localhost:8080/output.html");
		assertThat(fetchAllLinks.visitedLink.size(), is(1));
		assertThat(urldetails.getDomainLink().size(), is(0));
		assertThat(urldetails.getExternalLink().size(), is(4));
		assertThat(urldetails.getImgDetails().size(), is(1));
		wireMockServer.stop();
	}

}
