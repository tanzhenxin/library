package com.gtcc.library.entity;

/**
 * Created by LiuZ1 on 8/29/13.
 */
public class Borrow {
    private String userName;
    private String bookName;
    private String bookBianhao;
    private String borrowDate;
    private String planReturnDate;
    private String realReturnDate;
    private String ISBN;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookBianhao() {
        return bookBianhao;
    }

    public void setBookBianhao(String bookBianhao) {
        this.bookBianhao = bookBianhao;
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

    public String getISBN() {
        return ISBN;
    }

    public void setISBN(String ISBN) {
        this.ISBN = ISBN;
    }
}
