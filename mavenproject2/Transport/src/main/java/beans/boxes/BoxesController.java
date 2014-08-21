/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.boxes;

import db.Book;
import db.Box;
import db.helpers.TransportDbHelper;
import java.io.Serializable;
import java.util.List;
import java.util.Map;
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
public class BoxesController implements Serializable{

    private TransportDbHelper dbHelper;
    
    private Book book;
    private List<Box> boxes;
    
    private Box newBox;
    private Box selectedBox;
    
    /**
     * Creates a new instance of BoxesController
     */
    public BoxesController() {
        this.dbHelper = new TransportDbHelper();
        
        this.newBox = new Box();
    }

    @PostConstruct
    public void init() {
        initBook();
        initBoxes();
    }
    
    public String test(Box box) {
        this.selectedBox = box;
        return null;
    }
    
    public void addBox() {

        newBox.setBook(this.book);
        newBox.setBookspackage(this.book.getBookspackage());

        boolean added = this.dbHelper.boxes.updateBox(newBox);

        FacesMessage message;

        if (added) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Книгата е добавена!", null);
            this.boxes.add(0, newBox);

            this.newBox = new Box();
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Книгата не е добавена!", null);
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public String deleteBox() {
        String result = null;

        boolean isDeleted = false;
        FacesMessage message = null;

        try {
            isDeleted = this.dbHelper.boxes.deleteBox(this.selectedBox);
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            boolean isRemoved = this.boxes.remove(this.selectedBox);
            this.selectedBox = new Box();
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Кашонът е изтрит!", "");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Кашонът не е изтрит!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return result;
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            Box edited = (Box) event.getObject();

            boolean updated = this.dbHelper.boxes.updateBox(edited);

            if (updated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Кашонът е обновен!", "");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Кашонът не е обновен!", "");
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
    

    private void initBoxes() {
        this.boxes = this.dbHelper.boxes.getBoxesByBook(this.book);
    }
    
    private void initBook() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.book == null) {
            this.book = (Book) session.get("book");
        }
    }

    public Book getBook() {
        return book;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public List<Box> getBoxes() {
        return boxes;
    }

    public void setBoxes(List<Box> boxes) {
        this.boxes = boxes;
    }

    public Box getNewBox() {
        return newBox;
    }

    public void setNewBox(Box newBox) {
        this.newBox = newBox;
    }

    public Box getSelectedBox() {
        return selectedBox;
    }

    public void setSelectedBox(Box selectedBox) {
        this.selectedBox = selectedBox;
    }    
}
