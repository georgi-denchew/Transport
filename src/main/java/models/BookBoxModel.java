/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package models;

/**
 *
 * @author Georgi
 */
public class BookBoxModel {
    private String bookNumber;
    private String title;
    private Object booksCount;
    private Object boxesCount;
    
    public BookBoxModel(){
    }
    
    public BookBoxModel(String bookNumber, String title, Object booksCount, Object boxesCount) {
        this.bookNumber = bookNumber;
        this.title = title;
        this.booksCount = booksCount;
        this.boxesCount = boxesCount;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Object getBooksCount() {
        return booksCount;
    }

    public void setBooksCount(Object booksCount) {
        this.booksCount = booksCount;
    }

    public Object getBoxesCount() {
        return boxesCount;
    }

    public void setBoxesCount(Object boxesCount) {
        this.boxesCount = boxesCount;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }
}
