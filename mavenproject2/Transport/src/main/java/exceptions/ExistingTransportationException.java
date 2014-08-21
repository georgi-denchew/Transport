/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package exceptions;

/**
 *
 * @author Georgi
 */
public class ExistingTransportationException extends Exception {
    
    public ExistingTransportationException() {
        
    }
    
    public ExistingTransportationException(String message) {
        super(message);
    }
    
}
