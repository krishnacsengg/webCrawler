package com.restAPI.webCrawler.entities;

import java.util.ArrayList;
import java.util.List;

public class UrlDetails {

	private List<String> domainLink;
	private List<String> externalLink;
	private String linkResponse;
	private List<ImgDetails> imgDetails = new ArrayList<>();

	public UrlDetails(List<String> domainLink, List<String> externalLink, String linkResponse,
			List<ImgDetails> imgDetails) {
		this.domainLink = domainLink;
		this.externalLink = externalLink;
		this.linkResponse = linkResponse;
		this.imgDetails = imgDetails;
	}

	public List<String> getDomainLink() {
		return domainLink;
	}

	public void setDomainLink(List<String> domainLink) {
		this.domainLink = domainLink;
	}

	public List<String> getExternalLink() {
		return externalLink;
	}

	public void setExternalLink(List<String> externalLink) {
		this.externalLink = externalLink;
	}

	public String getLinkResponse() {
		return linkResponse;
	}

	public void setLinkResponse(String linkResponse) {
		this.linkResponse = linkResponse;
	}

	public List<ImgDetails> getImgDetails() {
		return imgDetails;
	}

	public void setImgDetails(List<ImgDetails> imgDetails) {
		this.imgDetails = imgDetails;
	}

	@Override
	public String toString() {
		return "UrlDetails [domainLink=" + domainLink + ", externalLink=" + externalLink + ", linkResponse="
				+ linkResponse + ", imgDetails=" + imgDetails + "]";
	}

}

