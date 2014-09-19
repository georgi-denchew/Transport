/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package models;

/**
 *
 * @author Georgi
 */
public class BookspackageCMRModel {
    private String packageNumber;
    private Double totalWeight;

    public BookspackageCMRModel(String packageNumber, Double totalWeight) {
        this.packageNumber = packageNumber;
        this.totalWeight = totalWeight;
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
}
