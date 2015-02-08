/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.models;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 *
 * @author Georgi
 */
public class TruckGroupTotalsModel implements Serializable {

    private Integer id;
    private String name;
    private Integer packagesCount;
    private Integer arrivedWeight;
    private Double totalPrice;

    public TruckGroupTotalsModel(Integer id, String name, Integer packagesCount, Integer arrivedWeight, Double totalPrice) {
        this.id = id;
        this.name = name;
        this.packagesCount = packagesCount;
        this.arrivedWeight = arrivedWeight;
        this.totalPrice = totalPrice;
    }

    public TruckGroupTotalsModel(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public TruckGroupTotalsModel() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getPackagesCount() {
        return packagesCount;
    }

    public void setPackagesCount(Integer packagesCount) {
        this.packagesCount = packagesCount;
    }

    public Integer getArrivedWeight() {
        return arrivedWeight;
    }

    public void setArrivedWeight(Integer arrivedWeight) {
        this.arrivedWeight = arrivedWeight;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TruckGroupTotalsModel) {
            return this.getId().equals(((TruckGroupTotalsModel) obj).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }

}
