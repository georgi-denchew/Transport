/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.bookspackage;

import com.griffinslogistics.mail.MailSender;
import db.Bookspackage;
import db.Transportation;
import db.helpers.TransportDbHelper;
import java.io.Serializable;
import java.security.Security;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import org.primefaces.event.RowEditEvent;
import utilities.Utilities;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BookspackageController implements Serializable {

    private final TransportDbHelper dbHelper;

    private List<Integer> weekNumbers;
    private List<Integer> years;

    private Integer year;
    private Integer weekNumber;

    private Transportation transportationForBookspackage;
    private List<Bookspackage> bookspackagesForTransport;
    private Bookspackage selectedBookspackage;
    private Bookspackage newBookspackage;

    public BookspackageController() {
        this.dbHelper = new TransportDbHelper();
        this.selectedBookspackage = new Bookspackage();
        this.newBookspackage = new Bookspackage();
    }

    @PostConstruct
    public void init() {
        initTransportation();
        initYears();

        this.year = this.years.get(0);
        onYearChange();
    }

    public void onYearChange() {
        this.weekNumbers = this.dbHelper.transportations.getCreatedWeeksForYear(this.year);
    }

    public void onWeekChange() {
        //use select items - transportation as value and weeknumber as label
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            Bookspackage edited = (Bookspackage) event.getObject();

            boolean isUpdated = this.dbHelper.bookspackages.updateBookspackage(edited);

            if (isUpdated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Пратката е обновена!", "");
                sendEmail(edited);
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Пратката не е обновена!", "");
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

    public String deleteBookspackage() {
        String result = null;

        boolean isDeleted = false;
        FacesMessage message = null;

        try {
            isDeleted = this.dbHelper.bookspackages.deleteBookspackage(this.selectedBookspackage);
            this.bookspackagesForTransport.remove(this.selectedBookspackage);
            this.selectedBookspackage = new Bookspackage();
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            boolean isRemoved = this.bookspackagesForTransport.remove(selectedBookspackage);
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Пратката е изтрита!", "");
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Пратката не е изтрита!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return result;
    }

    public String viewBooks(Bookspackage bookspackage) {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();
        session.put("bookspackage", bookspackage);
        return "/books/books?faces-redirect=true";
    }

    public void addBookspackage() {
        boolean added = false;

        this.newBookspackage.setTransportation(this.transportationForBookspackage);

        String biggestNumber = this.dbHelper.bookspackages.getBiggestBookspackageNumberForTransportation(this.transportationForBookspackage);

        String newNumber = Utilities.generateUniqueNumber(
                biggestNumber,
                this.transportationForBookspackage.getYear(),
                this.transportationForBookspackage.getWeekNumber());

        this.newBookspackage.setPackageNumber(newNumber);
        added = this.dbHelper.bookspackages.updateBookspackage(this.newBookspackage);

        FacesMessage message;

        if (added) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Пратката е добавена!", "");
            this.bookspackagesForTransport.add(0, this.newBookspackage);
            sendEmail(this.newBookspackage);
            this.newBookspackage = new Bookspackage();
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Пратката не е добавена!", null);
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    private void initTransportation() {
        Map<String, Object> session = FacesContext.getCurrentInstance().getExternalContext().getSessionMap();

        if (this.transportationForBookspackage == null) {
            this.transportationForBookspackage = (Transportation) session.get("transportation");
        }

        if (transportationForBookspackage != null) {
            this.bookspackagesForTransport = this.dbHelper.bookspackages.getBookspackagesByTransport(transportationForBookspackage);
        }
    }

    private void initYears() {
        this.years = this.dbHelper.transportations.getAllTransportationYears();
    }

    public Transportation getTransportationForBookspackage() {
        return transportationForBookspackage;
    }

    public void setTransportationForBookspackage(Transportation transportationForBookspackage) {
        this.transportationForBookspackage = transportationForBookspackage;
    }

    public List<Bookspackage> getBookspackagesForTransport() {
        return bookspackagesForTransport;
    }

    public void setBookspackagesForTransport(List<Bookspackage> bookspackagesForTransport) {
        this.bookspackagesForTransport = bookspackagesForTransport;
    }

    public Bookspackage getSelectedBookspackage() {
        return selectedBookspackage;
    }

    public void setSelectedBookspackage(Bookspackage selectedBookspackage) {
        this.selectedBookspackage = selectedBookspackage;
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

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeekNumber() {
        return weekNumber;
    }

    public void setWeekNumber(Integer weekNumber) {
        this.weekNumber = weekNumber;
    }

    public Bookspackage getNewBookspackage() {
        return newBookspackage;
    }

    public void setNewBookspackage(Bookspackage newBookspackage) {
        this.newBookspackage = newBookspackage;
    }

    //TODO: set actual message and settings
    private void sendEmail(Bookspackage bookspackage) {

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // Send an email if the edited package is for the current transportation and 
        // if it's thursday or later that week
        //if (transportationForBookspackage.getWeekNumber() == currentWeek && currentDayOfWeek >= Calendar.THURSDAY) {
            String messageText = String.format(
                    "Пратка с номер %s за транспорт %d/%d беше добавена или променена.",
                    bookspackage.getPackageNumber(),
                    transportationForBookspackage.getWeekNumber(),
                    transportationForBookspackage.getYear());
            MailSender.sendMail(messageText);
            
            
        //}

    }
}
