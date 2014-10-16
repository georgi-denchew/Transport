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
public class BookspackageCMRModel {
    private String packageNumber;
    private Double totalWeight;
    private Long totalBoxesCount;

    public BookspackageCMRModel(Long totalBoxesCount, String packageNumber, Double totalWeight) {
        this.packageNumber = packageNumber;
        this.totalWeight = totalWeight;
        this.totalBoxesCount = totalBoxesCount;
    }

    public BookspackageCMRModel() {
    }

    public String getPackageNumber() {
        return packageNumber;
    }

    public void setPackageNumber(String packageNumber) {
        this.packageNumber = packageNumber;
    }

    public double getTotalWeight() {
        return totalWeight;
    }

    public void setTotalWeight(Double totalWeight) {
        this.totalWeight = totalWeight;
    }

    public Long getTotalBoxesCount() {
        return totalBoxesCount;
    }

    public void setTotalBoxesCount(Long totalBoxesCount) {
        this.totalBoxesCount = totalBoxesCount;
    }
    
    
}
