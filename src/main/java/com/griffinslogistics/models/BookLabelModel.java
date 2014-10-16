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
public class BookLabelModel {
    private String title;
    private int bookNumber;
    private String client;
    private String address;
    private String postalCode;
    private String transportNumber;
    private Long count;

    public BookLabelModel(String address, String postalCode, String title, int bookNumber, String client, String transportNumber, Long count) {
        this.address = address;
        this.postalCode = postalCode;
        this.title = title;
        this.bookNumber = bookNumber;
        this.client = client;
        this.transportNumber = transportNumber;
        this.count = count;
    }

    public BookLabelModel() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setClient(String Client) {
        this.client = Client;
    }

    public String getTransportNumber() {
        return transportNumber;
    }

    public void setTransportNumber(String transportNumber) {
        this.transportNumber = transportNumber;
    }

    public Long getCount() {
        return count;
    }

    public void setCount(Long count) {
        this.count = count;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }
}
