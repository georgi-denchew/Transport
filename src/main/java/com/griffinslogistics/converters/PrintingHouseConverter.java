/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.converters;

import com.griffinslogistics.beans.books.BooksController;
import com.griffinslogistics.db.entities.PrintingHouse;
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
public class PrintingHouseConverter implements Converter {
    
    @ManagedProperty(value="#{booksController}")
    BooksController booksController;
    
    public Object getAsObject(FacesContext facesContext, UIComponent component, String submittedValue) {
        if (submittedValue.trim().equals("")) {
            return null;
        } else {
            try {
                int number = Integer.parseInt(submittedValue);

                for (PrintingHouse printingHouse : booksController.getAllPrintingHouses()) {
                    if (printingHouse.getId() == number) {
                        return printingHouse;
                    }
                }

            } catch(NumberFormatException exception) {
                throw new ConverterException(new FacesMessage(FacesMessage.SEVERITY_ERROR, "Conversion Error", "Невалидна печатница!"));
            }
        }

        return null;
    }

    public String getAsString(FacesContext facesContext, UIComponent component, Object value) {
        if (value == null || value.equals("")) {
            return "";
        } else {
            int id= ((PrintingHouse) value).getId();
            return String.valueOf(id);
        }
    }

    public BooksController getBooksController() {
        return booksController;
    }

    public void setBooksController(BooksController booksController) {
        this.booksController = booksController;
    }
}
