/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.beans.books;

import com.griffinslogistics.db.entities.PrintingHouse;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.helpers.TransportDbHelper;
import com.griffinslogistics.excel.BookLabelGenerator;
import com.griffinslogistics.models.BookForTransportationModel;
import com.griffinslogistics.models.BookLabelModel;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
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
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.*;

/**
 *
 * @author Georgi
 */
@ManagedBean
@ViewScoped
public class BooksForTransportationController implements Serializable {

    private static final Logger logger = Logger.getLogger(BooksController.class.getName());

    private final TransportDbHelper dbHelper;
    private Transportation transportation;
    List<BookForTransportationModel> booksForTransportation;
    List<BookForTransportationModel> selectedBooksForPrinting;
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

    public void getLabel() {
        OutputStream outputStream = null;

        if (selectedBooksForPrinting == null || selectedBooksForPrinting.isEmpty()) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Няма избрани или налични книги", null);
            FacesContext.getCurrentInstance().addMessage(null, message);
            return;
        }

        try {
            List<Integer> selectedBookIds = new ArrayList<Integer>();
            for (BookForTransportationModel bookForTransportationModel : selectedBooksForPrinting) {
                selectedBookIds.add(bookForTransportationModel.getId());
            }

            List<BookLabelModel> bookLabelModelList = this.dbHelper.books.getLabelInfoForBooks(selectedBookIds);

            ExternalContext externalContext = FacesContext.getCurrentInstance().getExternalContext();
            externalContext.responseReset();

            String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
            externalContext.setResponseContentType(contentType);
            String filename = String.format("\"Stikeri Knigi za Transport %s\\%s\"", transportation.getYear(), transportation.getWeekNumber());
            String responseHeaderValue = ("attachment; filename=" + filename + ".xlsx");
            externalContext.setResponseHeader("Content-Disposition", responseHeaderValue);
            outputStream = externalContext.getResponseOutputStream();

            BookLabelGenerator.generateLabels(outputStream, bookLabelModelList);
            FacesContext.getCurrentInstance().responseComplete();

        } catch (FileNotFoundException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
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

    public void postProcessXLS(Object document) {
        XSSFWorkbook wb = (XSSFWorkbook) document;

        try {
            XSSFSheet sheet = wb.getSheetAt(0);

            for (int i = 2; i <= sheet.getPhysicalNumberOfRows(); i++) {
                Row row = sheet.getRow(i);
                row.setHeightInPoints(30);

                XSSFCell cell = sheet.getRow(i).getCell(0);
                String text = cell.getStringCellValue();
                boolean value = Boolean.parseBoolean(text);

                if (value) {
                    cell.setCellValue("Да");
                } else {
                    cell.setCellValue("Не");
                }
            }
        } catch (Exception e) {
            String message = e.getMessage();
            e.printStackTrace();
        }
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

    public List<BookForTransportationModel> getSelectedBooksForPrinting() {
        return selectedBooksForPrinting;
    }

    public void setSelectedBooksForPrinting(List<BookForTransportationModel> selectedBooksForPrinting) {
        this.selectedBooksForPrinting = selectedBooksForPrinting;
    }
}
