/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.entities;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Georgi
 */
public class PrintingHouse {
    private Integer id;
    private String name;
    
    private List<Book> books = new ArrayList<Book>();
    
    public PrintingHouse(){
    }

    public PrintingHouse(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }    
    
    @Override
    public boolean equals(Object obj){
        if (obj instanceof PrintingHouse){
            return this.getId().equals(((PrintingHouse) obj).getId());
        }
        
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + (this.id != null ? this.id.hashCode() : 0);
        return hash;
    }
}
