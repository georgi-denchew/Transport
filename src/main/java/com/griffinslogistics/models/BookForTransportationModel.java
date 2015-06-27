/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.models;

import com.griffinslogistics.db.entities.Book;

/**
 *
 * @author Georgi
 */
public class BookForTransportationModel implements java.io.Serializable {

    private Integer id;
    private String bookspackageNumber;
    private String printingHouseName;
    private int bookNumber;
    private String title;
    private int count;
    private double weight;
    private double weightPerBook;
    private boolean discarded;
    private String deliveryAddress;

    public BookForTransportationModel(Integer id, String bookspackageNumber, String printingHouseName, int bookNumber, String title, int count, double weight, double weightPerBook, boolean discarded, String deliveryAddress) {
         this.id = id;
        this.bookspackageNumber = bookspackageNumber;
        this.printingHouseName = printingHouseName;
        this.bookNumber = bookNumber;
        this.title = title;
        this.count = count;
        this.weight = weight;
        this.weightPerBook = weightPerBook;
        this.discarded = discarded;
        this.deliveryAddress = deliveryAddress;
    }

    public static BookForTransportationModel fromBook(Book book) {
        BookForTransportationModel bookForTransportationModel
                = new BookForTransportationModel(book.getId(), book.getBookspackage().getPackageNumber(),
                        book.getPrintingHouse() != null ? book.getPrintingHouse().getName() : null,
                        book.getBookNumber(), book.getTitle(), book.getCount(), book.getWeight(),
                        book.getWeightPerBook(), book.isDiscarded(), book.getDeliveryAddress());

        return bookForTransportationModel;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getBookspackageNumber() {
        return bookspackageNumber;
    }

    public void setBookspackageNumber(String bookspackageNumber) {
        this.bookspackageNumber = bookspackageNumber;
    }

    public String getPrintingHouseName() {
        return printingHouseName;
    }

    public void setPrintingHouse(String printingHouseName) {
        this.printingHouseName = printingHouseName;
    }

    public int getBookNumber() {
        return bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getWeightPerBook() {
        return weightPerBook;
    }

    public void setWeightPerBook(double weightPerBook) {
        this.weightPerBook = weightPerBook;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}