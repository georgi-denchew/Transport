/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package enums;

/**
 *
 * За вдигане - Вдигнато - Доставено - 
 * фактурирано - Платено. 
 * (общо 5 варианта)
 *
 * @author Georgi
 */
public enum DeliveryStatus {

    TO_LIFT("toLift", "За вдигане"),
    LIFTED("lifted", "Вдигнато"),
    DELIVERED("delivered", "Доставено"),
    BILLED("billed", "Фактурирано"),
    PAID("paid", "Платено");

    String value;
    String displayValue;

    DeliveryStatus(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }
    
    public String getValue(){
        return this.value;
    }
    
    public String getDisplayValue(){
        return this.displayValue;
    }
    
    public static DeliveryStatus byValue(String value) {
        for (DeliveryStatus deliveryStatus : DeliveryStatus.values()) {
            String statusValue = deliveryStatus.getValue();
            if (statusValue.equals(value)) {
                return deliveryStatus;
            }
        }
        
        return null;
    }
}
