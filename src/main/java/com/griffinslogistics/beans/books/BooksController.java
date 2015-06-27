/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.books;

import com.griffinslogistics.excel.BookLabelGenerator;
import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Bookspackage;
import com.griffinslogistics.db.entities.PrintingHouse;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.helpers.TransportDbHelper;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import com.griffinslogistics.models.BookLabelModel;
import com.griffinslogistics.models.BookModel;
import java.util.ArrayList;
import javax.faces.model.SelectItem;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BooksController implements Serializable {

    private static final Logger logger = Logger.getLogger(BooksController.class.getName());

    private final TransportDbHelper dbHelper;

    private Transportation transportation;
    private Bookspackage bookspackage;
    private List<Book> books;
    private List<BookModel> allBookModels;

    private Book selectedBook;
    private Book newBook;

    int selectedBookModelBookNumber;

    private boolean isNewTitle;
    private BookModel selectedModel;

    private List<SelectItem> printingHousesSelectItems;
    private List<SelectItem> printingHousesFilterSelectItems;
    private List<PrintingHouse> allPrintingHouses;
    private boolean isForTransportation;

    public BooksController() {
        this.dbHelper = new TransportDbHelper();
        this.selectedBook = new Book();
        this.newBook = new Book();
    }

    @PostConstruct
    public void init() {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        String isForTransportationString = request.getParameter("for-transportation");

        this.isForTransportation = false;

        if (isForTransportationString != null) {
            this.isForTransportation = Boolean.parseBoolean(isForTransportationString);
        }

        initTransportation();
        if (!isForTransportation) {
            initBookspackage();
        }

        initBooks();
        initTranposrtationBooks();
        initPrintingHouses();

        if (this.allBookModels != null) {
            isNewTitle = this.allBookModels.isEmpty();
        }
    }

    public void getLabel() {
        OutputStream outputStream = null;

        try {
            BookLabelModel bookLabelModel = this.dbHelper.books.getLabelInfoForBook(selectedBook);

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            externalContext.setResponseContentType(contentType);
            String filename = String.format("\"Stiker Kniga %s Pratka %s\"",selectedBook.getBookNumber(), bookspackage.getPackageNumber());
            String responseHeaderValue = ("attachment; filename=" + filename + ".xlsx");
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            BookLabelGenerator.generateLabel(outputStream, bookLabelModel);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addBook() {
        newBook.setBookspackage(this.bookspackage);
        newBook.setDeliveryAddress(this.bookspackage.getDeliveryAddress());
        calculateWeightPerBook(newBook);
        Transportation currentTransportation = this.bookspackage.getTransportation();
        newBook.setTransportation(currentTransportation);

        if (this.isNewTitle) {
            Integer biggestNumber = this.dbHelper.books.getBiggestBookNumberForTransportation(currentTransportation);

            if (biggestNumber == null || biggestNumber == 0) {
                biggestNumber = 1;
            } else {
                biggestNumber++;
            }
            newBook.setBookNumber(biggestNumber);
        } else {
            findBookModel();
            newBook.setBookNumber(this.selectedModel.getBookNumber());
            newBook.setTitle((String) this.selectedModel.getTitle());
        }

        boolean added = this.dbHelper.books.updateBook(newBook);

        FacesMessage message;

        if (added) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е добавена!", null);
            this.books.add(0, newBook);

            if (this.isNewTitle) {
                this.allBookModels.add(new BookModel(this.newBook.getBookNumber(), this.newBook.getTitle()));
            }
            this.newBook = new Book();
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Книгата не е добавена!", null);
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String deleteBook() {
        String result = null;

        boolean isDeleted = false;
        FacesMessage message = null;

        try {
            isDeleted = this.dbHelper.books.deleteBook(this.selectedBook);
            if (isDeleted) {
                initTranposrtationBooks();
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            this.books.remove(this.selectedBook);
            this.selectedBook = new Book();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е изтрита!", "");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Книгата не е изтрита!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return result;
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            Book edited = (Book) event.getObject();
            calculateWeightPerBook(edited);

            if (!isForTransportation) {
                edited.setBookspackage(this.bookspackage);
            }

            edited.setTransportation(transportation);
            boolean updated = this.dbHelper.books.updateBook(edited);

            if (updated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е обновена!", "");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Книгата не е обновена!", "");
            }

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

    public String viewBookspackages() {

        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();

        session.put("book", this.selectedBook);
        session.put("transportation", this.transportation);
        return "/bookspackages/bookspackages?faces-redirect=true&book-filter=true";
    }

    public String viewBoxes(Book book) {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("book", book);
        return "/boxes/boxes?faces-redirect=true";
    }

    private void initTransportation() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.transportation == null) {
            this.transportation = (Transportation) session.get("transportation");
        }
    }

    private void initBookspackage() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.bookspackage == null) {
            this.bookspackage = (Bookspackage) session.get("bookspackage");
        }
    }

    private void initBooks() {
        if (isForTransportation) {
            this.books = this.dbHelper.books.getBooksByTransportation(this.transportation);
        } else {
            this.books = this.dbHelper.books.getBooksByBookspackage(this.bookspackage);
        }
    }

    private void initTranposrtationBooks() {
        if (isForTransportation) {
            this.allBookModels = this.dbHelper.books.getBookModelsByTransportation(this.transportation);
        } else {
            this.allBookModels = this.dbHelper.books.getBookModelsByTransportation(this.bookspackage.getTransportation());
        }
    }

    private void findBookModel() {
        for (BookModel model : allBookModels) {
            if (model.getBookNumber() == this.selectedBookModelBookNumber) {
                this.selectedModel = model;
                break;
            }
        }
    }

    private void initPrintingHouses() {
        this.printingHousesSelectItems = new ArrayList<SelectItem>();
        this.printingHousesFilterSelectItems = new ArrayList<SelectItem>();

        this.allPrintingHouses = this.dbHelper.printingHouses.getAllPrintingHouses();

        printingHousesFilterSelectItems.add(new SelectItem("", "Всички"));

        for (PrintingHouse printingHouse : allPrintingHouses) {
            printingHousesSelectItems.add(new SelectItem(printingHouse.getId(), printingHouse.getName()));
            printingHousesFilterSelectItems.add(new SelectItem(printingHouse.getId(), printingHouse.getName()));
        }
    }

    private void calculateWeightPerBook(Book selectedBook) {
        double weightPerBook = 0;
        if (selectedBook.getWeight() != 0 && selectedBook.getCount() != 0) {
            weightPerBook = selectedBook.getWeight() / selectedBook.getCount();
        }
        selectedBook.setWeightPerBook(weightPerBook);
    }

    public Bookspackage getBookspackage() {
        return bookspackage;
    }

    public void setBookspackage(Bookspackage bookspackage) {
        this.bookspackage = bookspackage;
    }

    public Transportation getTransportation() {
        return transportation;
    }

    public void setTransportation(Transportation transportation) {
        this.transportation = transportation;
    }

    public List<Book> getBooks() {
        return books;
    }

    public void setBooks(List<Book> books) {
        this.books = books;
    }

    public Book getSelectedBook() {
        return selectedBook;
    }

    public Book getNewBook() {
        return newBook;
    }

    public void setNewBook(Book newBook) {
        this.newBook = newBook;
    }

    public void setSelectedBook(Book selectedBook) {
        this.selectedBook = selectedBook;
    }

    public boolean isIsNewTitle() {
        return isNewTitle;
    }

    public void setIsNewTitle(boolean isNewTitle) {
        this.isNewTitle = isNewTitle;
    }

    public int getSelectedBookModelBookNumber() {
        return selectedBookModelBookNumber;
    }

    public void setSelectedBookModelBookNumber(int selectedBookModelTitle) {
        this.selectedBookModelBookNumber = selectedBookModelTitle;
    }

    public List<BookModel> getAllBookModels() {
        return allBookModels;
    }

    public void setAllBookModels(List<BookModel> allBookModels) {
        this.allBookModels = allBookModels;
    }

    public List<PrintingHouse> getAllPrintingHouses() {
        return allPrintingHouses;
    }

    public void setAllPrintingHouses(List<PrintingHouse> allPrintingHouses) {
        this.allPrintingHouses = allPrintingHouses;
    }

    public List<SelectItem> getPrintingHousesSelectItems() {
        return printingHousesSelectItems;
    }

    public void setPrintingHousesSelectItems(List<SelectItem> printingHousesSelectItems) {
        this.printingHousesSelectItems = printingHousesSelectItems;
    }

    public List<SelectItem> getPrintingHousesFilterSelectItems() {
        return printingHousesFilterSelectItems;
    }

    public void setPrintingHousesFilterSelectItems(List<SelectItem> printingHousesFilterSelectItems) {
        this.printingHousesFilterSelectItems = printingHousesFilterSelectItems;
    }

    public boolean isIsForTransportation() {
        return isForTransportation;
    }

    public void setIsForTransportation(boolean isForTransportation) {
        this.isForTransportation = isForTransportation;
    }
}
