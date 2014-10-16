/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.utilities;

import java.util.Calendar;
import java.util.Date;

/**
 *
 * @author Georgi
 */
public class Utilities {

    private static final int FIRST_BOOKSPACKAGE_NUMBER = 1;
    private static final String BOOKSPACKAGE_NUMBER_SPLITTER = "-";

    /**
     * Generates an unique number required for both <code>Book</code> and
     * <code>Bookspackage</code> entities
     *
     * @param biggestNumber Biggest number for the current list so far
     * @param year Year of the parent's <code>Transportation</code> entity
     * @param week Week of the parent's <code>Transportation</code> entity
     * @return generated unique number in the context of the parent entity
     */
    public static String generateUniqueBookspackageNumber(String biggestNumber, int year, int week) {
        String newNumber;

        if (biggestNumber != null) {
            String[] numberParts = biggestNumber.split(BOOKSPACKAGE_NUMBER_SPLITTER);

            int yearPosition = numberParts.length - 1;
            String toParse = numberParts[0];
            int incrementalNumber = Integer.parseInt(toParse);
            incrementalNumber++;

            newNumber
                    = incrementalNumber
                    + BOOKSPACKAGE_NUMBER_SPLITTER + numberParts[1]
                    + BOOKSPACKAGE_NUMBER_SPLITTER + numberParts[yearPosition];
        } else {
            newNumber
                    = String.valueOf(FIRST_BOOKSPACKAGE_NUMBER)
                    + BOOKSPACKAGE_NUMBER_SPLITTER
                    + week
                    + BOOKSPACKAGE_NUMBER_SPLITTER
                    // get last two numbers of the year
                    + (year - 2000);
        }

        return newNumber;
    }

    public static String generateDeliveryNumber(String dbBiggestDeliveryNumber, int week, int year) {
        
        String result = null;
        if (dbBiggestDeliveryNumber == null) {
            
            result = String.format("G-%s-1-%s", year, week);
        } else {
            String[] deliveryNumberArray = dbBiggestDeliveryNumber.split("-");
            int incrementedNumber = Integer.parseInt(deliveryNumberArray[2]) + 1;
            result = String.format("G-%s-%s-%s", year, incrementedNumber, week);
        }
        
        return result;
    }
}
