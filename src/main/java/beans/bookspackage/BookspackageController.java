/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.bookspackage;

import com.griffinslogistics.excel.BDLGenerator;
import com.griffinslogistics.excel.CMRGenerator;
import com.griffinslogistics.mail.MailSender;
import db.Bookspackage;
import db.Pulsiodetails;
import db.Transportation;
import db.TruckGroup;
import db.helpers.TransportDbHelper;
import enums.TruckGroupEnum;
import exceptions.ConcurentUpdateException;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import javax.faces.model.SelectItem;
import models.BookBoxModel;
import models.BookspackageCMRModel;
import org.apache.commons.io.output.TeeOutputStream;
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

    private List<SelectItem> truckGroupSelectItems;
    private List<SelectItem> truckGroupFilterSelectItems;
    private List<TruckGroup> allTruckGroups;
    private TruckGroup selectedTruckGroup;

    private Integer year;
    private Integer weekNumber;

    private Transportation transportationForBookspackage;
    private List<Bookspackage> bookspackagesForTransport;
    private Bookspackage selectedBookspackage;
    private Bookspackage newBookspackage;

    private Map<String, Double> totalWeights;
    private List<String> totalWeightsKeys;

    public BookspackageController() {
        this.dbHelper = new TransportDbHelper();
        this.selectedBookspackage = new Bookspackage();
        this.newBookspackage = new Bookspackage();
        this.selectedTruckGroup = new TruckGroup();
    }

    @PostConstruct
    public void init() {
        initTransportation();
        initYears();
        initTruckGroups();
        initTotals();
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
            this.selectedTruckGroup.setId(edited.getTruckGroup().getId());
            TruckGroup truckGroup = findTruckGroup();
            edited.setTruckGroup(truckGroup);

            boolean isUpdated = this.dbHelper.bookspackages.updateBookspackage(edited);

            if (isUpdated) {
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Пратката е обновена!", "");

                if (edited.getTruckGroup() == null) {
                    edited.setTruckGroup(new TruckGroup());
                }

                initTotals();
            } else {
                message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Пратката не е обновена!", "");
            }

        } catch (ConcurentUpdateException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            this.initTransportation();
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
            TruckGroup truckGroup = this.selectedBookspackage.getTruckGroup();
            if (truckGroup.getId() == null || truckGroup.getId() == 0) {
                this.selectedBookspackage.setTruckGroup(null);
            }
            
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

        String newNumber = Utilities.generateUniqueBookspackageNumber(
                biggestNumber,
                this.transportationForBookspackage.getYear(),
                this.transportationForBookspackage.getWeekNumber());

        this.newBookspackage.setPackageNumber(newNumber);

        FacesMessage message;

        try {
            added = this.dbHelper.bookspackages.updateBookspackage(this.newBookspackage);
        } catch (ConcurentUpdateException e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            this.initTransportation();
        }

        if (added) {
            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Пратката е добавена!", "");
            this.newBookspackage.setTruckGroup(new TruckGroup());
            this.bookspackagesForTransport.add(0, this.newBookspackage);
            sendEmail(this.newBookspackage);
            this.newBookspackage = new Bookspackage();
        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка! Пратката не е добавена!", null);
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public void getAllBDL() {
        OutputStream outputStream = null;

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.ms-excel";
            externalContext.setResponseContentType(contentType);
            String responseHeaderValue = String.format("attachment; filename=\"BDL Transport %s\"", this.transportationForBookspackage.getWeekNumber() + "/" + this.transportationForBookspackage.getYear());
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            Map<String, List<BookBoxModel>> bookBoxModelsForTransportation = this.dbHelper.books.getAllBookBoxModelsByTransportation(this.transportationForBookspackage);
            Pulsiodetails pulsioDetails = this.dbHelper.pulsio.getDetails();

            BDLGenerator.generateAll(outputStream, bookBoxModelsForTransportation, pulsioDetails);

            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void getBDL() {
        OutputStream outputStream = null;

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.ms-excel";
            externalContext.setResponseContentType(contentType);
            String responseHeaderValue = String.format("attachment; filename=\"BDL %s\"", selectedBookspackage.getPackageNumber());
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            List<BookBoxModel> bookBoxModels = this.dbHelper.books.getAllBookBoxModelsByPackage(selectedBookspackage);
            Pulsiodetails pulsioDetails = this.dbHelper.pulsio.getDetails();

            BDLGenerator.generateSingle(outputStream, bookBoxModels, pulsioDetails);

            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void getAllCMR() {
        OutputStream outputStream = null;

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            externalContext.setResponseContentType(contentType);
            String responseHeaderValue = String.format("attachment; filename=\"CMR Transport %s\"", this.transportationForBookspackage.getWeekNumber() + "/" + this.transportationForBookspackage.getYear());
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            List<BookspackageCMRModel> bookspackageCMRModels = this.dbHelper.bookspackages.totalWeightForBookspackages(this.transportationForBookspackage);
            Pulsiodetails pulsioDetails = this.dbHelper.pulsio.getDetails();

            CMRGenerator.generateAll(outputStream, bookspackageCMRModels, pulsioDetails);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void getCMR() {
        OutputStream outputStream = null;

        try {
            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            externalContext.setResponseContentType(contentType);
            String responseHeaderValue = String.format("attachment; filename=\"CMR %s\"", selectedBookspackage.getPackageNumber());
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            BookspackageCMRModel bookspackageCMRModel = this.dbHelper.bookspackages.totalWeightForBookspackage(selectedBookspackage);
            Pulsiodetails pulsioDetails = this.dbHelper.pulsio.getDetails();

            CMRGenerator.generateSingle(outputStream, bookspackageCMRModel, pulsioDetails);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                outputStream.close();
            } catch (IOException ex) {
                Logger.getLogger(BookspackageController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
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

    //TODO: set actual message and settings
    private void sendEmail(Bookspackage bookspackage) {

        Date currentDate = new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(currentDate);

        int currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK);
        int currentWeek = calendar.get(Calendar.WEEK_OF_YEAR);

        // Send an email if the edited package is for the current transportation and 
        // if it's thursday or later that week
        if (transportationForBookspackage.getWeekNumber() == currentWeek && currentDayOfWeek >= Calendar.THURSDAY) {
            String messageText = String.format(
                    "Пратка с номер %s за транспорт %d/%d беше добавена или променена.",
                    bookspackage.getPackageNumber(),
                    transportationForBookspackage.getWeekNumber(),
                    transportationForBookspackage.getYear());
            MailSender.sendMail(messageText);

        }
    }

    private void initTruckGroups() {
        this.truckGroupSelectItems = new ArrayList<SelectItem>();
        this.truckGroupFilterSelectItems = new ArrayList<SelectItem>();

        allTruckGroups = this.dbHelper.bookspackages.getAllTruckGroups();

        truckGroupFilterSelectItems.add(new SelectItem("", "Всички"));

        for (TruckGroup truckGroup : allTruckGroups) {
            truckGroupSelectItems.add(new SelectItem(truckGroup.getId(), truckGroup.getName()));
            truckGroupFilterSelectItems.add(new SelectItem(truckGroup.getId(), truckGroup.getName()));
        }
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

    public List<SelectItem> getTruckGroupSelectItems() {
        return truckGroupSelectItems;
    }

    public void setTruckGroupSelectItems(List<SelectItem> truckGroupSelectItems) {
        this.truckGroupSelectItems = truckGroupSelectItems;
    }

    public TruckGroup getSelectedTruckGroup() {
        return selectedTruckGroup;
    }

    public void setSelectedTruckGroup(TruckGroup selectedTruckGroup) {
        this.selectedTruckGroup = selectedTruckGroup;
    }

    public Map<String, Double> getTotalWeights() {
        return totalWeights;
    }

    public void setTotalWeights(Map<String, Double> totalWeights) {
        this.totalWeights = totalWeights;
    }

    public List<String> getTotalWeightsKeys() {
        return totalWeightsKeys;
    }

    public void setTotalWeightsKeys(List<String> totalWeightsKeys) {
        this.totalWeightsKeys = totalWeightsKeys;
    }

    public List<SelectItem> getTruckGroupFilterSelectItems() {
        return truckGroupFilterSelectItems;
    }

    public void setTruckGroupFilterSelectItems(List<SelectItem> truckGroupFilterSelectItems) {
        this.truckGroupFilterSelectItems = truckGroupFilterSelectItems;
    }

    private TruckGroup findTruckGroup() {
        for (TruckGroup truckGroup : allTruckGroups) {
            if (truckGroup.getId().equals(this.selectedTruckGroup.getId())) {
                return truckGroup;
            }
        }

        return null;
    }

    private void initTotals() {
        totalWeights = this.dbHelper.bookspackages.totalWeightsForTransportation(this.transportationForBookspackage);
        this.totalWeightsKeys = new ArrayList<String>(totalWeights.keySet());
    }
}
