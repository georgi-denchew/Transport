/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.enums;

/**
 *
 * @author Georgi
 */
public enum BookspackagePriorityEnum {

    NEUTRAL("neutral", "Неутрална"),
    VERY_IMPORTANT("very_important", "Много важна"),
    URGENT("urgent", "Спешна");

    String value;
    String displayValue;

    BookspackagePriorityEnum(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public String getValue() {
        return this.value;
    }
    
    public String getDisplayValue(){
        return this.displayValue;
    }

    public static BookspackagePriorityEnum byValue(String value) {
        for (BookspackagePriorityEnum bookspackagePriority : BookspackagePriorityEnum.values()) {
            String bookspackagePriorityValue = bookspackagePriority.getValue();
            if (bookspackagePriorityValue.equals(value)) {
                return bookspackagePriority;
            }
        }

        return null;
    }
}
