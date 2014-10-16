/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.entities;

/**
 *
 * @author Georgi
 */
public class Pulsiodetails {
    private Integer id;
    private String contact1;
    private String contact2;
    private String password;
    private byte[] logo;
    private byte[] signature;
    
    public Pulsiodetails(){
    }

    public Pulsiodetails(Integer id, String contact1, String contact2, String password, byte[] logo, byte[] signature) {
        this.id = id;
        this.contact1 = contact1;
        this.contact2 = contact2;
        this.password = password;
        this.logo = logo;
        this.signature = signature;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContact1() {
        return contact1;
    }

    public void setContact1(String contact1) {
        this.contact1 = contact1;
    }

    public String getContact2() {
        return contact2;
    }

    public void setContact2(String contact2) {
        this.contact2 = contact2;
    }

    public byte[] getLogo() {
        return logo;
    }

    public void setLogo(byte[] logo) {
        this.logo = logo;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
