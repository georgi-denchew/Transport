package com.griffinslogistics.db.entities;
// Generated Jul 28, 2014 10:58:21 PM by Hibernate Tools 3.6.0

import java.util.HashSet;
import java.util.Set;

/**
 * Book generated by hbm2java
 */
public class Book implements java.io.Serializable {

    private Integer id;
    private Bookspackage bookspackage;
    private Transportation transportation;
    private PrintingHouse printingHouse;
    private int bookNumber;
    private String title;
    private int count;
    private double weight;
    private double weightPerBook;
    private Set boxes = new HashSet(0);
    private boolean discarded;
    private String deliveryAddress;

    private Long totalBooksCount;
    private Double totalBooksWeight;

    public Book() {
    }

    public Book(Bookspackage bookspackage, Transportation transportation, int bookNumber,
            String title, int count, double weight, Long totalBooksCount, Double totalBooksWeight, boolean discarded) {
        this.bookspackage = bookspackage;
        this.transportation = transportation;
        this.bookNumber = bookNumber;
        this.title = title;
        this.count = count;
        this.weight = weight;
        this.totalBooksCount = totalBooksCount;
        this.totalBooksWeight = totalBooksWeight;
        this.discarded = discarded;
    }

    public Book(Bookspackage bookspackage, Transportation transportation, int bookNumber,
            String title, int count, double weight, double weightPerBook,
            Long totalBooksCount, Double totalBooksWeight, Set boxes, boolean discarded) {
        this.bookspackage = bookspackage;
        this.transportation = transportation;
        this.bookNumber = bookNumber;
        this.title = title;
        this.count = count;
        this.weight = weight;
        this.boxes = boxes;
        this.weightPerBook = weightPerBook;
        this.totalBooksCount = totalBooksCount;
        this.totalBooksWeight = totalBooksWeight;
        this.discarded = discarded;
    }

    public Integer getId() {
        return this.id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bookspackage getBookspackage() {
        return this.bookspackage;
    }

    public void setBookspackage(Bookspackage bookspackage) {
        this.bookspackage = bookspackage;
    }

    public Transportation getTransportation() {
        return this.transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public int getBookNumber() {
        return this.bookNumber;
    }

    public void setBookNumber(int bookNumber) {
        this.bookNumber = bookNumber;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getCount() {
        return this.count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public double getWeight() {
        return this.weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public Set getBoxes() {
        return this.boxes;
    }

    public void setBoxes(Set boxes) {
        this.boxes = boxes;
    }

    public double getWeightPerBook() {
        return weightPerBook;
    }

    public void setWeightPerBook(double weightPerBook) {
        this.weightPerBook = weightPerBook;
    }

    public Long getTotalBooksCount() {
        return totalBooksCount;
    }

    public void setTotalBooksCount(Long totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public Double getTotalBooksWeight() {
        return totalBooksWeight;
    }

    public void setTotalBooksWeight(Double totalBooksWeight) {
        this.totalBooksWeight = totalBooksWeight;
    }

    public boolean isDiscarded() {
        return discarded;
    }

    public void setDiscarded(boolean discarded) {
        this.discarded = discarded;
    }

    public PrintingHouse getPrintingHouse() {
        return printingHouse;
    }

    public void setPrintingHouse(PrintingHouse printingHouse) {
        this.printingHouse = printingHouse;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }
}
