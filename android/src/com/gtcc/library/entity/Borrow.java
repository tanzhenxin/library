package com.gtcc.library.entity;

/**
 * Created by LiuZ1 on 8/29/13.
 */
public class Borrow {
    private String userName;
    private String borrowDate;
    private String planReturnDate;
    private String realReturnDate;
    private Book book;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBorrowDate() {
        return borrowDate;
    }

    public void setBorrowDate(String borrowDate) {
        this.borrowDate = borrowDate;
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

    public Book getBook() {
    	return book;
    }
    
    public void setBook(Book book) {
    	this.book = book;
    }
}
