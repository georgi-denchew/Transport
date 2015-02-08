/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.models;

import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Bookspackage;
import com.griffinslogistics.db.entities.BookspackageHistory;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.entities.TruckGroup;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Georgi
 */
public class BookspackageViewModel {
        private Integer id;
    private Integer version;
    private Transportation transportation;
    private TruckGroup truckGroup;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String packageNumber;
    private String merchant;
    private String client;
    private String priority;
    private Date deliveryDate;
    private String deliveryAddress;
    private String remarks;
    private String printDeliveryDay;
    private List<Book> books;
    private Set boxes = new HashSet(0);
    private List<BookspackageHistory> bookspackageHistories;
    private Integer totalBooksCount;
    private Integer totalOrderedBooksCount;
    private Double totalBooksWeight;
    private Double totalOrderedBooksWeight;
    
    private String displayPriority;

    
    
    public BookspackageViewModel(Bookspackage bookspackage){
        
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public TruckGroup getTruckGroup() {
        return truckGroup;
    }

    public void setTruckGroup(TruckGroup truckGroup) {
        this.truckGroup = truckGroup;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public String getMerchant() {
        return merchant;
    }

    public void setMerchant(String merchant) {
        this.merchant = merchant;
    }

    public String getClient() {
        return client;
    }

    public void setClient(String client) {
        this.client = client;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public String getPrintDeliveryDay() {
        return printDeliveryDay;
    }

    public void setPrintDeliveryDay(String printDeliveryDay) {
        this.printDeliveryDay = printDeliveryDay;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Set getBoxes() {
        return boxes;
    }

    public void setBoxes(Set boxes) {
        this.boxes = boxes;
    }

    public List<BookspackageHistory> getBookspackageHistories() {
        return bookspackageHistories;
    }

    public void setBookspackageHistories(List<BookspackageHistory> bookspackageHistories) {
        this.bookspackageHistories = bookspackageHistories;
    }

    public Integer getTotalBooksCount() {
        return totalBooksCount;
    }

    public void setTotalBooksCount(Integer totalBooksCount) {
        this.totalBooksCount = totalBooksCount;
    }

    public Integer getTotalOrderedBooksCount() {
        return totalOrderedBooksCount;
    }

    public void setTotalOrderedBooksCount(Integer totalOrderedBooksCount) {
        this.totalOrderedBooksCount = totalOrderedBooksCount;
    }

    public Double getTotalBooksWeight() {
        return totalBooksWeight;
    }

    public void setTotalBooksWeight(Double totalBooksWeight) {
        this.totalBooksWeight = totalBooksWeight;
    }

    public Double getTotalOrderedBooksWeight() {
        return totalOrderedBooksWeight;
    }

    public void setTotalOrderedBooksWeight(Double totalOrderedBooksWeight) {
        this.totalOrderedBooksWeight = totalOrderedBooksWeight;
    }

    public String getDisplayPriority() {
        return displayPriority;
    }

    public void setDisplayPriority(String displayPriority) {
        this.displayPriority = displayPriority;
    }
    
    
    
    
    
}
