/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.enums;

/**
 *
 * За вдигане - Вдигнато - Доставено - 
 * фактурирано - Платено. 
 * (общо 5 варианта)
 *
 * @author Georgi
 */
public enum DeliveryStatusEnum {

    TO_LIFT("toLift", "За вдигане"),
    LIFTED("lifted", "Вдигнато"),
    DELIVERED("delivered", "Доставено"),
    BILLED("billed", "Фактурирано"),
    PAID("paid", "Платено");

    String value;
    String displayValue;

    DeliveryStatusEnum(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }
    
    public String getValue(){
        return this.value;
    }
    
    public String getDisplayValue(){
        return this.displayValue;
    }
    
    public static DeliveryStatusEnum byValue(String value) {
        for (DeliveryStatusEnum deliveryStatus : DeliveryStatusEnum.values()) {
            String statusValue = deliveryStatus.getValue();
            if (statusValue.equals(value)) {
                return deliveryStatus;
            }
        }
        
        return null;
    }
}
