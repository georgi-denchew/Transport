/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

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
    public static String generateUniqueNumber(String biggestNumber, int year, int week) {
        String newNumber;

        if (biggestNumber != null) {
            String[] numberParts = biggestNumber.split(BOOKSPACKAGE_NUMBER_SPLITTER);

            int lastPosition = numberParts.length - 1;
            String toParse = numberParts[lastPosition];
            int lastNumber = Integer.parseInt(toParse);
            lastNumber++;

            newNumber = numberParts[0]
                    + BOOKSPACKAGE_NUMBER_SPLITTER + numberParts[1]
                    + BOOKSPACKAGE_NUMBER_SPLITTER + lastNumber;
        } else {
            newNumber = year + BOOKSPACKAGE_NUMBER_SPLITTER
                    + week + BOOKSPACKAGE_NUMBER_SPLITTER
                    + String.valueOf(FIRST_BOOKSPACKAGE_NUMBER);
        }

        return newNumber;
    }
}
