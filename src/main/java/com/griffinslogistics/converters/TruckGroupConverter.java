/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.converters;

import com.griffinslogistics.beans.bookspackage.BookspackageController;
import com.griffinslogistics.beans.delivery.DeliveryController;
import com.griffinslogistics.db.entities.Deliverydirection;
import com.griffinslogistics.db.entities.TruckGroup;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

/**
 *
 * @author Georgi
 */
@ManagedBean
@RequestScoped
public class TruckGroupConverter implements Converter {
    
    @ManagedProperty(value="#{bookspackageController}")
    BookspackageController bookspackageController;
    
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);

                for (TruckGroup truckGroup : bookspackageController.getAllTruckGroups()) {
                    if (truckGroup.getId() == number) {
                        return truckGroup;
                    }
                }

            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Невалидна посока!"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            int id= ((TruckGroup) value).getId();
            return String.valueOf(id);
        }
    }

    public BookspackageController getBookspackageController() {
        return bookspackageController;
    }

    public void setBookspackageController(BookspackageController bookspackageController) {
        this.bookspackageController = bookspackageController;
    }
}
