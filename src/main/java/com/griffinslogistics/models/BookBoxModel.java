/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.models;

/**
 *
 * @author Georgi
 */
public class BookBoxModel {
    private int bookNumber;
    private String title;
    private String client;
    private String deliveryAddress;
    private Object booksCount;
    private Object boxesCount;
    
    public BookBoxModel(){
    }
    
    public BookBoxModel(int bookNumber, String title, Object booksCount, Object boxesCount) {
        this.bookNumber = bookNumber;
        this.title = title;
        this.booksCount = booksCount;
        this.boxesCount = boxesCount;
    }

    public BookBoxModel(int bookNumber, String title) {
        this.bookNumber = bookNumber;
        this.title = title;
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

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
