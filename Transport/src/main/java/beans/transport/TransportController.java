/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.transport;

import beans.bookspackage.BookspackageController;
import db.Transportation;
import db.helpers.TransportDbHelper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import org.primefaces.event.RowEditEvent;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class TransportController implements Serializable {

    private final TransportDbHelper dbHelper;
    private Calendar calendar;

    private Transportation newTransport;
    private List<Integer> weekNumbers;
    private List<Integer> years;
    private List<Transportation> allTransportations;
    private Transportation selectedTransportation;

    public TransportController() {
        this.dbHelper = new TransportDbHelper();

        this.newTransport = new Transportation();
        this.allTransportations = new ArrayList<Transportation>();
    }

    @PostConstruct
    public void init() {
        this.allTransportations = this.dbHelper.transportations.getAllTransportations();

        Collections.reverse(allTransportations);

        this.calendar = Calendar.getInstance();
        initWeekNumbers();
        initYears();
    }

    public String addTransport() {
        String resultPage = null;

        calendar.setWeekDate(newTransport.getYear(), newTransport.getWeekNumber(), Calendar.MONDAY);

        Date date = calendar.getTime();
        newTransport.setStartDate(date);

        boolean exists = this.dbHelper.transportations.existsTransportation(newTransport);

        FacesMessage message = null;

        if (exists) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Вече съществува транспорт за тази седмица!", "");
            FacesContext.getCurrentInstance().addMessage(null, message);
            return resultPage;
        }

        boolean added = this.dbHelper.transportations.addTransportation(newTransport);

        if (added) {
            this.allTransportations.add(0, newTransport);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Транспортът е добавен", "");
            newTransport = new Transportation();
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Грешка при добавяне на транспорта!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return resultPage;
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            Transportation edited = (Transportation) event.getObject();

            calendar.setWeekDate(edited.getYear(), edited.getWeekNumber(), Calendar.MONDAY);
            Date date = calendar.getTime();
            edited.setStartDate(date);

            boolean updated = this.dbHelper.transportations.addTransportation(edited);

            if (updated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Транспортът е обновен!", "");
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Транспортът не е обновен!", "");
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

    public String deleteTransportation() {
        String result = "";
        boolean isDeleted = false;
        FacesMessage message = null;

        try {
            isDeleted = this.dbHelper.transportations.deleteTransportation(selectedTransportation);
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            this.allTransportations.remove(selectedTransportation);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Транспортът е изтрит!", "");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Транспортът не е изтрит!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return result;
    }

    public String viewBookspackages(Transportation transportation) {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("transportation", transportation);
        return "/bookspackages/bookspackages?faces-redirect=true";
    }
    
    public String viewBooks(Transportation transportation) {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("transportationForBooks", transportation);
        return "/books/books-for-transportation?faces-redirect=true";
    }

    private void initWeekNumbers() {
        int weeksCount = this.calendar.getActualMaximum(Calendar.WEEK_OF_YEAR);

        this.weekNumbers = new ArrayList<Integer>(weeksCount);

        for (int i = 0; i < weeksCount; i++) {
            this.weekNumbers.add(i, i + 1);
        }
    }

    private void initYears() {
        this.years = new ArrayList<Integer>(3);
        int currentYear = this.calendar.get(Calendar.YEAR);

        this.years.add(0, currentYear);
        this.years.add(1, currentYear - 1);
        this.years.add(2, currentYear + 1);
    }

    public Transportation getNewTransport() {
        return newTransport;
    }

    public void setNewTransport(Transportation newTransport) {
        this.newTransport = newTransport;
    }

    public List<Integer> getWeekNumbers() {
        return weekNumbers;
    }

    public void setWeekNumbers(List<Integer> weekNumbers) {
        this.weekNumbers = weekNumbers;
    }

    public List<Integer> getYears() {
        return years;
    }

    public void setYears(List<Integer> years) {
        this.years = years;
    }

    public List<Transportation> getAllTransportations() {
        return allTransportations;
    }

    public void setAllTransportations(List<Transportation> allTransportations) {
        this.allTransportations = allTransportations;
    }

    public Transportation getSelectedTransportation() {
        return selectedTransportation;
    }

    public void setSelectedTransportation(Transportation selectedTransportation) {
        this.selectedTransportation = selectedTransportation;
    }
}
