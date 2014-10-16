/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.validators;

import com.griffinslogistics.constants.DeliveryConstants;
import com.griffinslogistics.db.helpers.PulsioDetailsHelper;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.FacesValidator;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

/**
 *
 * @author Georgi
 */
@FacesValidator("deleteDeliveryValidator")
public class DeleteDeliveryValidator implements Validator{
    
    private static final PulsioDetailsHelper pulsioHelper = new PulsioDetailsHelper();
    
    @Override
    public void validate(FacesContext fc, UIComponent uic, Object o) throws ValidatorException {
        String password = (String) o;
        boolean isValid = pulsioHelper.validatePassword(password);
        
        if (!isValid){
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Грешна парола!", null);
            
            throw new ValidatorException(message);
        }
        
//        if (!password.equals(DeliveryConstants.DELETE_PASSWORD)){
//            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Грешна парола!", null);
//            
//            throw new ValidatorException(message);
//        }
    }
    
}
