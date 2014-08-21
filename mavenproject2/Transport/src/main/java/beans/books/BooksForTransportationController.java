/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.books;

import db.Book;
import db.Transportation;
import db.helpers.TransportDbHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import models.BookExtendedModel;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BooksForTransportationController implements Serializable {

    private TransportDbHelper dbHelper;
    private Transportation transportation;

    List<BookExtendedModel> booksForTransportation;

    /**
     * Creates a new instance of BooksForTransportController
     */
    public BooksForTransportationController() {
        this.dbHelper = new TransportDbHelper();
        this.booksForTransportation = new ArrayList<BookExtendedModel>();
    }

    @PostConstruct
    public void init() {
        initTransporation();
        initBooksForTransportation();
    }

    private void initTransporation() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.transportation == null) {
            this.transportation = (Transportation) session.get("transportationForBooks");
        }
    }

    private void initBooksForTransportation() {
        this.booksForTransportation = this.dbHelper.books.getAllBooksByTransportation(transportation);
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public List<BookExtendedModel> getBooksForTransportation() {
        return booksForTransportation;
    }

    public void setBooksForTransportation(List<BookExtendedModel> booksForTransportation) {
        this.booksForTransportation = booksForTransportation;
    }
}
