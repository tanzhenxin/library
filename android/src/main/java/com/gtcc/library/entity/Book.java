package com.gtcc.library.entity;

import java.io.Serializable;


public class Book implements Serializable {
	private static final long serialVersionUID = 1L;

    private String objectId;
    private String tag;
	private String title;
	private String author;
	private String description;
	private String imageUrl;
	private String price;
	private String ISBN;
	private String publisher;
	private String publishedDate;
    private int printLength;
	private String category;

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) { this.objectId = objectId; }

    public String getTag() { return tag; }

    public void setTag(String tag) { this.tag = tag; }

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getAuthor() {
		return author;
	}
	
	public void setAuthor(String author) {
		author = author.replace("\"", "");
		if (author.startsWith("["))
			author = author.substring(1, author.length() - 1);
		this.author = author;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imgUrl) {
		this.imageUrl = imgUrl;
	}

	public String getPrice() {
		return price;
	}

	public void setPrice(String price) {
		this.price = price;
	}

	public String getISBN() {
		return ISBN;
	}

	public void setISBN(String iSBN) {
		ISBN = iSBN;
	}

	public String getPublisher() {
		return publisher;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getPublishedDate() {
		return publishedDate;
	}

	public void setPublishedDate(String publishDate) {
		this.publishedDate = publishDate;
	}

    public int getPrintLength() { return printLength; }

    public void setPrintLength(int printLength) { this.printLength = printLength; }
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result + ((imageUrl == null) ? 0 : imageUrl.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Book other = (Book) obj;
		return getObjectId().equals(other.getObjectId());
	}
}