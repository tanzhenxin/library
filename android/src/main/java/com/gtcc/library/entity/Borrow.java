package com.gtcc.library.entity;

/**
 * Created by LiuZ1 on 8/29/13.
 */
public class Borrow {
    private String objectId;
    private String username;
    private String startBorrowDate;
    private String planReturnDate;
    private String realReturnDate;
    private String bookTag;
    private Book book;

    public String getObjectId() { return objectId; }

    public void setObjectId(String objectId) {
        this.objectId = objectId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getStartBorrowDate() {
        return startBorrowDate;
    }

    public void setStartBorrowDate(String startBorrowDate) {
        this.startBorrowDate = startBorrowDate;
    }

    public String getPlanReturnDate() {
        return planReturnDate;
    }

    public void setPlanReturnDate(String planReturnDate) {
        this.planReturnDate = planReturnDate;
    }

    public String getRealReturnDate() {
        return realReturnDate;
    }

    public void setRealReturnDate(String realReturnDate) {
        this.realReturnDate = realReturnDate;
    }

    public String getBookTag() { return bookTag; }

    public void setBookTag(String bookTag) {
        this.bookTag = bookTag;
    }

    public Book getBook() {
    	return book;
    }
    
    public void setBook(Book book) {
    	this.book = book;
    }
}
