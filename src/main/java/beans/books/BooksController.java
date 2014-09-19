/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.books;

import db.Book;
import db.Bookspackage;
import db.Transportation;
import db.helpers.TransportDbHelper;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import models.BookModel;
import org.primefaces.event.RowEditEvent;
import utilities.Utilities;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BooksController implements Serializable {

    private final TransportDbHelper dbHelper;

    private Bookspackage bookspackage;
    private List<Book> books;
    private List<BookModel> allBookModels;

    private Book selectedBook;
    String selectedBookModelBookNumber;

    private boolean isNewTitle;
    private BookModel selectedModel;

    public BooksController() {
        this.dbHelper = new TransportDbHelper();
        this.selectedBook = new Book();
    }

    @PostConstruct
    public void init() {
        initBookspackage();
        initBooks();
        initTranposrtationBooks();
    }

    public void addBook() {

        selectedBook.setBookspackage(this.bookspackage);
        calculateWeightPerBook(selectedBook);
        Transportation currentTransportation = this.bookspackage.getTransportation();
        selectedBook.setTransportation(currentTransportation);

        if (this.isNewTitle) {
            String biggestNumber = this.dbHelper.books.getBiggestBookNumberForTransportation(currentTransportation);

            String newNumber = Utilities.generateUniqueBookspackageNumber(
                    biggestNumber,
                    currentTransportation.getYear(),
                    currentTransportation.getWeekNumber());

            selectedBook.setBookNumber(newNumber);
        } else {
            findBookModel();
            selectedBook.setBookNumber((String) this.selectedModel.getBookNumber());
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
            if (isDeleted){
                initTranposrtationBooks();
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            boolean isRemoved = this.books.remove(this.selectedBook);
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
    }

    private void initTranposrtationBooks() {
        this.allBookModels = this.dbHelper.books.getBookModelsByTransportation(this.bookspackage.getTransportation());
    }

    private void findBookModel() {
        for (BookModel model : allBookModels) {
            if (model.getBookNumber().equals(this.selectedBookModelBookNumber)) {
                this.selectedModel = model;
            }
        }
    }

    public Bookspackage getBookspackage() {
        return bookspackage;
    }

    public void setBookspackage(Bookspackage bookspackage) {
        this.bookspackage = bookspackage;
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

    public String getSelectedBookModelBookNumber() {
        return selectedBookModelBookNumber;
    }

    public void setSelectedBookModelBookNumber(String selectedBookModelTitle) {
        this.selectedBookModelBookNumber = selectedBookModelTitle;
    }

    public List<BookModel> getAllBookModels() {
        return allBookModels;
    }

    public void setAllBookModels(List<BookModel> allBookModels) {
        this.allBookModels = allBookModels;
    }

    private void calculateWeightPerBook(Book selectedBook) {
        double weightPerBook = selectedBook.getWeight() / selectedBook.getCount();
        selectedBook.setWeightPerBook(weightPerBook);
    }
}
