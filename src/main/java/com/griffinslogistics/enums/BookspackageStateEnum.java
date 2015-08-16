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

public enum BookspackageStateEnum {
    
    NEUTRAL("", "Неутрална"),
    TNT("TNT", "TNT"),
    FINANCE_DEPARTMENT("Финансов Отдел", "Финансов Отдел"),
    FOR_TUESDAY("За Вторник", "За Вторник");

    String value;
    String displayValue;

    BookspackageStateEnum(String value, String displayValue) {
        this.value = value;
        this.displayValue = displayValue;
    }

    public String getValue() {
        return this.value;
    }
    
    public String getDisplayValue(){
        return this.displayValue;
    }

    public static BookspackageStateEnum byValue(String value) {
        for (BookspackageStateEnum bookspackageState : BookspackageStateEnum.values()) {
            String bookspackageStateValue = bookspackageState.getValue();
            if (bookspackageStateValue.equals(value)) {
                return bookspackageState;
            }
        }

        return null;
    }
}
