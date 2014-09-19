/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package enums;

/**
 *
 * @author Georgi
 */
public enum TruckGroupEnum {

    ONE("One", 1),
    TWO("Two", 2),
    THREE("Three", 3),
    FOUR("Four", 4),
    FIVE("Five", 5),
    SIX("Six", 6),
    SEVEN("Seven", 7),
    EIGHT("Eight", 8),
    NINE("Nine", 9),
    TEN("Ten", 10);
    
    String label;
    int value;
    
    TruckGroupEnum(String label, int value) {
        this.label = label;
        this.value = value;
    }

    public String getLabel() {
        return label;
    }

    public int getValue() {
        return value;
    }
    
    public static TruckGroupEnum byValue(int value){
        for (TruckGroupEnum truckGroup : TruckGroupEnum.values()) {
            int truckGroupValue = truckGroup.getValue();
            if (truckGroupValue == value) {
                return truckGroup;
            }
        }
        
        return null;
    }
    
}
