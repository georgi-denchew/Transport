/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.models;

import java.io.Serializable;

/**
 *
 * @author Georgi
 */
public class BookExtendedModel implements Serializable {

    private String title;
    private String bookNumber;
    private String packageNumber;
    private Long count;

    public BookExtendedModel() {
    }

    public BookExtendedModel(String title, String bookNumber, Long count) {
        this.title = title;
        this.bookNumber = bookNumber;
        this.count = count;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(String bookNumber) {
        this.bookNumber = bookNumber;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }
    
    
}
