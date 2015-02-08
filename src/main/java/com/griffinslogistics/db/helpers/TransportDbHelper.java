/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.helpers;

import java.io.Serializable;

/**
 *
 * @author Georgi
 */
public class TransportDbHelper implements Serializable{
    public final DeliveriesHelper deliveries;
    public final TransporationsHelper transportations;
    public final BookspackagesHelper bookspackages;
    public final BooksHelper books;
    public final BoxesHelper boxes;
    public final PulsioDetailsHelper pulsio;
    public final PrintingHousesHelper printingHouses;
    public final AdditionalCostsHelper additionalCosts;
    
    public TransportDbHelper() {
        this.deliveries = new DeliveriesHelper();
        this.transportations = new TransporationsHelper();
        this.bookspackages = new BookspackagesHelper();
        this.books = new BooksHelper();
        this.boxes = new BoxesHelper();
        this.pulsio = new PulsioDetailsHelper();
        this.printingHouses = new PrintingHousesHelper();
        this.additionalCosts = new AdditionalCostsHelper();
    }    

}
