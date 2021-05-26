package com.restAPI.webCrawler.entities;

public class ImgDetails {

	private String imgSrc;
	private String imgDescription;
	
	public ImgDetails(String imgSrc, String imgDescription) {
		this.imgSrc = imgSrc;
		this.imgDescription = imgDescription;
	}
	
	public String getImgSrc() {
		return imgSrc;
	}
	
	public void setImgSrc(String imgSrc) {
		this.imgSrc = imgSrc;
	}
	
	public String getImgDescription() {
		return imgDescription;
	}
	
	public void setImgDescription(String imgDescription) {
		this.imgDescription = imgDescription;
	}

	@Override
	public String toString() {
		return "ImgDetails [imgSrc=" + imgSrc + ", imgDescription=" + imgDescription + "]";
	}
	
	
}