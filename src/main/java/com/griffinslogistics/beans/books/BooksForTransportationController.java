/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.books;

import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.PrintingHouse;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.helpers.TransportDbHelper;
import com.griffinslogistics.models.BookForTransportationModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BooksForTransportationController implements Serializable {

    private final TransportDbHelper dbHelper;
    private Transportation transportation;

    List<BookForTransportationModel> booksForTransportation;

    private List<SelectItem> printingHousesFilterSelectItems;
    private List<PrintingHouse> allPrintingHouses;

    /**
     * Creates a new instance of BooksForTransportController
     */
    public BooksForTransportationController() {
        this.dbHelper = new TransportDbHelper();
        this.booksForTransportation = new ArrayList<BookForTransportationModel>();
    }

    @PostConstruct
    public void init() {
        initTransporation();
        initBooksForTransportation();
        initPrintingHouses();
    }

    public void updateDiscardedField(BookForTransportationModel book) {

        boolean updateSuccessful = this.dbHelper.books.updateBookDiscardedField(book.getId(), book.isDiscarded());
        FacesMessage message;
        String messageString = null;

        if (updateSuccessful) {

            if (book.isDiscarded()) {
                if (book.getPrintingHouseName() != null) {
                    messageString = String.format("Книга със заглавие \"%s\" за пратка \"%s\" от печатница \"%s\" е отменена", book.getTitle(), book.getBookNumber(), book.getPrintingHouseName());
                } else {
                    messageString = String.format("Книга със заглавие \"%s\" за пратка \"%s\" е отменена", book.getTitle(), book.getBookNumber());
                }
            } else {
                if (book.getPrintingHouseName() != null) {
                    messageString = String.format("Книга със заглавие \"%s\" за пратка \"%s\" от печатница \"%s\" не е отменена", book.getTitle(), book.getBookNumber(), book.getPrintingHouseName());
                } else {
                    messageString = String.format("Книга със заглавие \"%s\" за пратка \"%s\" не е отменена", book.getTitle(), book.getBookNumber());
                }
            }
            message = new FacesMessage(messageString);

        } else {
            messageString = String.format("Възникна грешка! Моля опитайте отново.");
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, messageString, null);
        }
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private void initTransporation() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.transportation == null) {
            this.transportation = (Transportation) session.get("transportation");
        }
    }

    private void initBooksForTransportation() {
        this.booksForTransportation = this.dbHelper.books.getBooksForTransportation(transportation.getId());

    }

    private void initPrintingHouses() {
        this.printingHousesFilterSelectItems = new ArrayList<SelectItem>();

        this.allPrintingHouses = this.dbHelper.printingHouses.getAllPrintingHouses();

        printingHousesFilterSelectItems.add(new SelectItem("", "Всички"));

        for (PrintingHouse printingHouse : allPrintingHouses) {
            printingHousesFilterSelectItems.add(new SelectItem(printingHouse.getName(), printingHouse.getName()));
        }
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public List<BookForTransportationModel> getBooksForTransportation() {
        return booksForTransportation;
    }

    public void setBooksForTransportation(List<BookForTransportationModel> booksForTransportation) {
        this.booksForTransportation = booksForTransportation;
    }

    public List<SelectItem> getPrintingHousesFilterSelectItems() {
        return printingHousesFilterSelectItems;
    }

    public void setPrintingHousesFilterSelectItems(List<SelectItem> printingHousesFilterSelectItems) {
        this.printingHousesFilterSelectItems = printingHousesFilterSelectItems;
    }
}
