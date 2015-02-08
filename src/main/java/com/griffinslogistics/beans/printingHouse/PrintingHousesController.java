/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.printingHouse;

import com.griffinslogistics.db.entities.PrintingHouse;
import com.griffinslogistics.db.helpers.TransportDbHelper;
import com.griffinslogistics.exceptions.ExistingEntityException;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class PrintingHousesController implements Serializable {

    private static final Logger logger = Logger.getLogger(PrintingHousesController.class.getName());

    private final TransportDbHelper dbHelper;

    private List<PrintingHouse> printingHouses;
    private PrintingHouse newPrintingHouse;

    public PrintingHousesController() {
        this.dbHelper = new TransportDbHelper();
        this.newPrintingHouse = new PrintingHouse();
    }

    @PostConstruct
    public void init() {
        this.printingHouses = this.dbHelper.printingHouses.getAllPrintingHouses();
    }

    public void addPrintingHouse() {
        FacesMessage message = null;
        try {
            this.newPrintingHouse.setName(this.newPrintingHouse.getName().trim());
            boolean added = this.dbHelper.printingHouses.addPrintingHouse(this.newPrintingHouse);

            if (added) {
                this.printingHouses.add(0, this.newPrintingHouse);
                this.newPrintingHouse = new PrintingHouse();
                
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Печатницата е добавена!", null);
            }
        } catch (ExistingEntityException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
//            Book edited = (Book) event.getObject();
//            calculateWeightPerBook(edited);
//            edited.setBookspackage(this.bookspackage);
//            edited.setTransportation(transportation);
//            boolean updated = this.dbHelper.books.updateBook(edited);

//            if (updated) {
//                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е обновена!", "");
//            } else {
//                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Книгата не е обновена!", "");
//            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void onCancelEdit(RowEditEvent event) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Обновлението е отказано!", "");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<PrintingHouse> getPrintingHouses() {
        return printingHouses;
    }

    public void setPrintingHouses(List<PrintingHouse> printingHouses) {
        this.printingHouses = printingHouses;
    }

    public PrintingHouse getNewPrintingHouse() {
        return newPrintingHouse;
    }

    public void setNewPrintingHouse(PrintingHouse newPrintingHouse) {
        this.newPrintingHouse = newPrintingHouse;
    }

}
