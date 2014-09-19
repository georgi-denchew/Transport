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
public class ConcurentUpdateException extends Exception {
    public ConcurentUpdateException() {
        super();
    }
    
    public ConcurentUpdateException(String message) {
        super(message);
    }
}
