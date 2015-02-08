/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.entities;

import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author Georgi
 */
public class BookspackageHistory implements Serializable {
    private Integer id;
    private Bookspackage bookspackage;
    private Date lastModification;
    private String country;
    private String postalCode;
    private String phoneNumber;
    private String email;
    private String merchant;
    private String client;
    private Double pricePerKilogram;
    private String priority;
    private Date deliveryDate;
    private String deliveryAddress;
    private String remarksSales;
    private String remarksLogistics;
    private String printDeliveryDay;
    private String truckGroupName;
    
    public BookspackageHistory() {
    }

    public BookspackageHistory(Integer id, Bookspackage bookspackage, String truckGroupName,
            Date lastModification, String country,String postalCode, String phoneNumber, 
            String email, String merchant, String client, Date deliveryDate, String deliveryAddress,
            String remarks, String printDeliveryDay, String priority) {
        this.id = id;
        this.bookspackage = bookspackage;
        this.lastModification = lastModification;
        this.country = country;
        this.postalCode = postalCode;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.merchant = merchant;
        this.client = client;
        this.deliveryDate = deliveryDate;
        this.deliveryAddress = deliveryAddress;
        this.remarksSales = remarks;
        this.printDeliveryDay = printDeliveryDay;
        this.truckGroupName = truckGroupName;
        this.priority = priority;
    }
    
    
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Bookspackage getBookspackage() {
        return bookspackage;
    }

    public void setBookspackage(Bookspackage bookspackage) {
        this.bookspackage = bookspackage;
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

    public String getRemarksSales() {
        return remarksSales;
    }

    public void setRemarksSales(String remarksSales) {
        this.remarksSales = remarksSales;
    }

    public String getRemarksLogistics() {
        return remarksLogistics;
    }
    public void setRemarksLogistics(String remarksLogstics) {
        this.remarksLogistics = remarksLogstics;
    }

    public Date getLastModification() {
        return lastModification;
    }

    public void setLastModification(Date lastModification) {
        this.lastModification = lastModification;
    }    

    public String getTruckGroupName() {
        return truckGroupName;
    }

    public void setTruckGroupName(String truckGroupName) {
        this.truckGroupName = truckGroupName;
    }

    public String getPrintDeliveryDay() {
        return printDeliveryDay;
    }

    public void setPrintDeliveryDay(String printDeliveryDay) {
        this.printDeliveryDay = printDeliveryDay;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Double getPricePerKilogram() {
        return pricePerKilogram;
    }

    public void setPricePerKilogram(Double pricePerKilogram) {
        this.pricePerKilogram = pricePerKilogram;
    }
}
