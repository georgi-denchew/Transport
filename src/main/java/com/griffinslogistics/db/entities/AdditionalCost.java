/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.entities;

import java.math.BigDecimal;

/**
 *
 * @author Georgi
 */
public class AdditionalCost implements java.io.Serializable {
    private Integer id;
    private String packageNumber;
    private String payer;
    private BigDecimal price;
    private Transportation transportation;
    private Integer transportationId;
    private String description;

    public AdditionalCost(Integer id, String packageNumber, String payer, BigDecimal price, Transportation transportation, Integer transportationId) {
        this.id = id;
        this.packageNumber = packageNumber;
        this.payer = payer;
        this.price = price;
        this.transportation = transportation;
        this.transportationId = transportationId;
    }

    public AdditionalCost() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public String getPayer() {
        return payer;
    }

    public void setPayer(String payer) {
        this.payer = payer;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public Integer getTransportationId() {
        return transportationId;
    }

    public void setTransportationId(Integer transportationId) {
        this.transportationId = transportationId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
