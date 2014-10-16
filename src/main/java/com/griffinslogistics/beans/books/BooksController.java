/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.books;

import com.griffinslogistics.excel.BookLabelGenerator;
import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Bookspackage;
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
    int selectedBookModelBookNumber;

    private boolean isNewTitle;
    private BookModel selectedModel;

    public BooksController() {
        this.dbHelper = new TransportDbHelper();
        this.selectedBook = new Book();
    }

    @PostConstruct
    public void init() {
        initTransportation();
        initBookspackage();
        initBooks();
        initTranposrtationBooks();
        
        if (this.allBookModels != null) {
            isNewTitle = this.allBookModels.isEmpty();
        }
    }

    public void getLabel() {
        OutputStream outputStream = null;

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            externalContext.setResponseContentType(contentType);
            String responseHeaderValue = String.format("attachment; filename=\"Sticker Kniga %s Pratka %s \"", selectedBook.getBookNumber(), bookspackage.getPackageNumber());
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            BookLabelModel bookLabelModel = this.dbHelper.books.getLabelInfoForBook(selectedBook);
            BookLabelGenerator.generateLabel(outputStream, bookLabelModel);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public void addBook() {

        selectedBook.setBookspackage(this.bookspackage);
        calculateWeightPerBook(selectedBook);
        Transportation currentTransportation = this.bookspackage.getTransportation();
        selectedBook.setTransportation(currentTransportation);

        if (this.isNewTitle) {
            Integer biggestNumber = this.dbHelper.books.getBiggestBookNumberForTransportation(currentTransportation);

            if (biggestNumber == null || biggestNumber == 0) {
                biggestNumber = 1;
            } else {
                biggestNumber++;
            }
            selectedBook.setBookNumber(biggestNumber);
        } else {
            findBookModel();
            selectedBook.setBookNumber(this.selectedModel.getBookNumber());
            selectedBook.setTitle((String) this.selectedModel.getTitle());
        }

        boolean added = this.dbHelper.books.updateBook(selectedBook);

        FacesMessage message;

        if (added) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е добавена!", null);
            this.books.add(0, selectedBook);

            if (this.isNewTitle) {
                this.allBookModels.add(new BookModel(this.selectedBook.getBookNumber(), this.selectedBook.getTitle()));
            }
            this.selectedBook = new Book();
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
            edited.setBookspackage(this.bookspackage);
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
        if (this.bookspackage != null) {
            this.books = this.dbHelper.books.getBooksByBookspackage(this.bookspackage);
        } 
//        else {
//        this.books = this.dbHelper.books.getAllBooks();
//        }
    }

    private void initTranposrtationBooks() {
        this.allBookModels = this.dbHelper.books.getBookModelsByTransportation(this.bookspackage.getTransportation());
    }

    private void findBookModel() {
        for (BookModel model : allBookModels) {
            if (model.getBookNumber() == this.selectedBookModelBookNumber) {
                this.selectedModel = model;
                break;
            }
        }
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

    private void calculateWeightPerBook(Book selectedBook) {
        double weightPerBook = 0;
        if (selectedBook.getWeight() != 0 && selectedBook.getCount() != 0) {
            weightPerBook = selectedBook.getWeight() / selectedBook.getCount();
        }
        selectedBook.setWeightPerBook(weightPerBook);
    }
}
