/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package beans.delivery;

import com.transport.DeliveryFTP;
import db.Attachment;
import db.Delivery;
import db.Deliverydirection;
import db.helpers.TransportDbHelper;
import enums.DeliveryStatus;
import exceptions.DeliveryFTPDeleteException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author Georgi Ivanov
 */
@ManagedBean
@ViewScoped
public class DeliveryController implements Serializable {

    private static final String DATATABLE_ID = "table-form:deliveryList";

    private String enteredPassword;
    
    private final TransportDbHelper dbHelper;

    private Delivery newDelivery;
    private Delivery selectedDelivery;
    private String includePage;

    private ArrayList<Delivery> allDeliveries;
    private List<UploadedFile> uploadedFiles;
    private List<Attachment> attachments;
    private StreamedContent downloadFile;
    private Deliverydirection direction;
    private int deliveryDirectionId;

    private List<Deliverydirection> deliveryDirectionsList;

    private SelectItem[] deliveryDirectionsSelectItems;
    private SelectItem[] deliveryDirectionsFilterSelectItems;

    private SelectItem[] deliveryStatusSelectItems;
    private SelectItem[] deliveryStatusFilterSelectItems;

    private DeliveryFTP ftp;

    public DeliveryController() {
        this.dbHelper = new TransportDbHelper();
    }

    @PostConstruct
    public void init() {    
        // TODO: @EJB annotation not working? 
        //deliveryHelper = new DeliveriesHelper();

        this.retrieveDeliveries();
        this.generateDeliveryDirectionsSelectItems();
        this.generateDeliveryStatusSelectItems();

        this.ftp = new DeliveryFTP();

        this.uploadedFiles = new ArrayList<UploadedFile>();
        this.attachments = new ArrayList<Attachment>();

        this.newDelivery = new Delivery();
        Deliverydirection dir = this.deliveryDirectionsList.get(0);
        this.newDelivery.setDeliverydirection(dir);
    }

    public String addNewDelivery() {
        String resultPage = null;

        DataTable datatable = (DataTable) FacesContext.getCurrentInstance().getViewRoot().findComponent(DATATABLE_ID);

        datatable.reset();

        FacesMessage message = null;

        try {
            this.setDeliveryDirection(this.newDelivery);
            String uuid = String.valueOf(UUID.randomUUID().getMostSignificantBits());
            this.newDelivery.setUuid(uuid);

            //boolean updated = this.deliveryHelper.updateDelivery(this.newDelivery);
            boolean updated = this.dbHelper.deliveries.updateDelivery(this.newDelivery);

            
            if (updated) {
                this.allDeliveries.add(0, this.newDelivery);

                for (UploadedFile file : this.uploadedFiles) {
                    this.saveFile(file, newDelivery);
                }

                this.newDelivery = new Delivery();

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Доставката е добавена", "");
            }

        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
        return resultPage;
    }

    public void addFile(FileUploadEvent event) {

        UploadedFile file = event.getFile();

        FacesMessage message = null;

        boolean saveSuccessful = this.saveFile(file, this.selectedDelivery);

//        if (saveSuccessful) {
//            msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Файлът с име: " + file.getFileName() + " е добавен!", "");
//        } else {
//            msg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка: файлът не е добавен!", "");
//        }
//        FacesContext.getCurrentInstance().addMessage(null, msg);
//        FacesContext.getCurrentInstance().getExternalContext().getFlash().setKeepMessages(true);
    }

    public void addFileToNewDelivery(FileUploadEvent event) {
        UploadedFile file = event.getFile();
        this.uploadedFiles.add(file);
    }

    public void fillAttachments(Delivery delivery) {
        Set<Attachment> all = delivery.getAttachments();
        this.attachments.clear();
        if (all != null) {
            this.attachments.addAll(all);
        } else {
            this.attachments = new ArrayList<Attachment>();
        }
    }

    public void onEdit(RowEditEvent event) {
        FacesMessage message = null;

        try {
            Delivery edited = (Delivery) event.getObject();
            Integer directionId = edited.getDeliverydirection().getId();

            //boolean updated = this.deliveryHelper.updateDelivery(edited);
            boolean updated = this.dbHelper.deliveries.updateDelivery(edited);

            
            if (updated) {
                Deliverydirection direction = null;

                for (Deliverydirection deliverydirection : this.deliveryDirectionsList) {
                    if (deliverydirection.getId().compareTo(directionId) == 0) {
                        direction = deliverydirection;
                    }
                }

                edited.setDeliverydirection(direction);

                message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Доставката е обновена!", "");
            }
        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        } finally {
            FacesContext.getCurrentInstance().addMessage(null, message);
        }
    }

    public void onCancelEdit(RowEditEvent event) {
        Delivery delivery = (Delivery) event.getObject();

        //boolean toDelete = this.deliveryHelper.isEmpty(delivery);
        boolean toDelete = this.dbHelper.deliveries.isEmpty(delivery);

        if (toDelete) {
            this.allDeliveries.remove(delivery);
        }

        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Обновлението е отказано!", "");
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public List<Attachment> getAttachments(Delivery delivery) {
        //List<Attachment> attachmentsForDelivery = this.deliveryHelper.getAttachments(delivery);
        List<Attachment> attachmentsForDelivery = this.dbHelper.deliveries.getAttachments(delivery);

        return attachmentsForDelivery;
    }

    public StreamedContent getDownloadFile(Attachment attachment) {

        FacesMessage message = null;

        try {
            downloadFile = null;
            ByteArrayOutputStream byteOutputStream = (ByteArrayOutputStream) ftp.downloadFile(attachment.getFilePath());
            byte[] bytes = byteOutputStream.toByteArray();
            ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);

            downloadFile = new DefaultStreamedContent(byteInputStream, attachment.getContentType(),
                    attachment.getName());

            byteOutputStream.close();
            byteInputStream.close();

            message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Доставката е обновена!", "");

        } catch (IOException e) {
            Logger.getLogger(DeliveryController.class.getName()).log(Level.SEVERE, null, e);
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);
        return downloadFile;
    }

    public String getDeliveryStatus(String state) {
        DeliveryStatus status = DeliveryStatus.byValue(state);

        if (status != null) {
            return status.getDisplayValue();
        } else {
            return "";
        }
    }

    public String deleteDelivery() {
        String result = "";
        boolean isDeleted = false;
        FacesMessage message = null;

        try {
            isDeleted = this.dbHelper.deliveries.deleteDelivery(selectedDelivery);

        } catch (Exception e) {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
            FacesContext.getCurrentInstance().addMessage(null, message);
        }

        if (isDeleted) {
            this.allDeliveries.remove(selectedDelivery);

            try {
                int deletedAttachmentsCount = this.ftp.deleteDirectory(selectedDelivery.getUuid());
                String formattedMessage = String.format("Доставката е изтрита заедно с %d прикачени файлове", deletedAttachmentsCount);
                message = new FacesMessage(FacesMessage.SEVERITY_INFO, formattedMessage, "");
            } catch (DeliveryFTPDeleteException ex) {
                message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Доставката е изтрита,"
                        + " но възникна проблем при изтриването на прикачените файлове!", "");
                Logger.getLogger(DeliveryController.class.getName()).log(Level.SEVERE, null, ex);
            }

        } else {
            message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Възникна грешка, доставката не е изтрита!", "");
        }

        FacesContext.getCurrentInstance().addMessage(null, message);

        return result;
    }

    private void retrieveDeliveries() {
//        this.allDeliveries = (ArrayList<Delivery>) deliveryHelper.getAllDeliveries();
        this.allDeliveries = (ArrayList<Delivery>) this.dbHelper.deliveries.getAllDeliveries();

    }

    private boolean saveFile(UploadedFile file, Delivery delivery) {
        boolean isSaved = false;

        try {
            FileInputStream inputStream = null;

            String contentType = file.getContentType();
            String fileName = file.getFileName();
            inputStream = (FileInputStream) file.getInputstream();

            //int deliveryId = delivery.getId();
            String uuid = delivery.getUuid();
            String filePath = ftp.uploadFile(uuid, inputStream, fileName);

            if (!filePath.equals("")) {
                Attachment newAttachment;
                newAttachment = new Attachment(filePath, contentType);
//                this.deliveryHelper.addAttachment(delivery, newAttachment);
                this.dbHelper.deliveries.addAttachment(delivery, newAttachment);

                isSaved = true;
            }

        } catch (IOException ex) {
            Logger.getLogger(DeliveryController.class.getName()).log(Level.SEVERE, null, ex);
        }

        return isSaved;
    }

    private void generateDeliveryDirectionsSelectItems() {
//        this.deliveryDirectionsList = deliveryHelper.getAllDeliveryDirections();
        this.deliveryDirectionsList = this.dbHelper.deliveries.getAllDeliveryDirections();

        int count = deliveryDirectionsList.size();
        this.deliveryDirectionsSelectItems = new SelectItem[count];
        this.deliveryDirectionsFilterSelectItems = new SelectItem[count + 1];

        deliveryDirectionsFilterSelectItems[0] = new SelectItem("", "Всички");

        for (int i = 0; i < count; i++) {
            SelectItem deliveryItem = new SelectItem(
                    deliveryDirectionsList.get(i).getId(),
                    deliveryDirectionsList.get(i).getDirection());

            this.deliveryDirectionsSelectItems[i] = deliveryItem;
            this.deliveryDirectionsFilterSelectItems[i + 1] = deliveryItem;
        }
    }

    private void setDeliveryDirection(Delivery delivery) {
        for (Deliverydirection deliverydirection : this.deliveryDirectionsList) {

            int id = deliverydirection.getId();

            if (id == this.deliveryDirectionId) {
                delivery.setDeliverydirection(deliverydirection);
                break;
            }
        }
    }

    private void generateDeliveryStatusSelectItems() {
        DeliveryStatus[] statuses = DeliveryStatus.values();

        this.deliveryStatusSelectItems = new SelectItem[statuses.length];
        this.deliveryStatusFilterSelectItems = new SelectItem[statuses.length + 1];
        this.deliveryStatusFilterSelectItems[0] = new SelectItem("", "Всички");

        for (int i = 0; i < statuses.length; i++) {
            this.deliveryStatusSelectItems[i] = new SelectItem(statuses[i].getValue(), statuses[i].getDisplayValue());
            this.deliveryStatusFilterSelectItems[i + 1] = new SelectItem(statuses[i].getValue(), statuses[i].getDisplayValue());
        }
    }

    public SelectItem[] getDeliveryStatusSelectItems() {
        return deliveryStatusSelectItems;
    }

    public void setDeliveryStatusSelectItems(SelectItem[] deliveryStatusSelectItems) {
        this.deliveryStatusSelectItems = deliveryStatusSelectItems;
    }

    public SelectItem[] getDeliveryStatusFilterSelectItems() {
        return deliveryStatusFilterSelectItems;
    }

    public void setDeliveryStatusFilterSelectItems(SelectItem[] deliveryStatusFilterSelectItems) {
        this.deliveryStatusFilterSelectItems = deliveryStatusFilterSelectItems;
    }

    public Deliverydirection getDirection() {
        return direction;
    }

    public void setDirection(Deliverydirection direction) {
        this.direction = direction;
    }

    public StreamedContent getDownloadFile() {
        return downloadFile;
    }

    public void setDownloadFile(StreamedContent downloadFile) {
        this.downloadFile = downloadFile;
    }

    public Delivery getSelectedDelivery() {
        return selectedDelivery;
    }

    public void setSelectedDelivery(Delivery selectedDelivery) {
        this.selectedDelivery = selectedDelivery;
    }

    public Delivery getNewDelivery() {
        return newDelivery;
    }

    public void setNewDelivery(Delivery newDelivery) {
        this.newDelivery = newDelivery;
    }

    public List<Delivery> getAllDeliveries() {
        return this.allDeliveries;
    }

    public void setAllDeliveries(ArrayList<Delivery> allDeliveries) {
        this.allDeliveries = allDeliveries;
    }

    public String getIncludePage() {
        return includePage;
    }

    public void setIncludePage(String includePage) {
        this.includePage = includePage;
    }

    public List<Attachment> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<Attachment> attachments) {
        this.attachments = attachments;
    }

    public List<UploadedFile> getUploadedFiles() {
        return uploadedFiles;
    }

    public void setUploadedFiles(List<UploadedFile> uploadedFiles) {
        this.uploadedFiles = uploadedFiles;
    }

    public List<Deliverydirection> getDeliveryDirectionsList() {
        return deliveryDirectionsList;
    }

    public void setDeliveryDirectionsList(List<Deliverydirection> deliveryDirectionsList) {
        this.deliveryDirectionsList = deliveryDirectionsList;
    }

    public SelectItem[] getDeliveryDirectionsSelectItems() {
        return deliveryDirectionsSelectItems;
    }

    public void setDeliveryDirectionsSelectItems(SelectItem[] deliveryDirectionsSelectItems) {
        this.deliveryDirectionsSelectItems = deliveryDirectionsSelectItems;
    }

    public int getDeliveryDirectionId() {
        return deliveryDirectionId;
    }

    public void setDeliveryDirectionId(int deliveryDirectionId) {
        this.deliveryDirectionId = deliveryDirectionId;
    }

    public SelectItem[] getDeliveryDirectionsFilterSelectItems() {
        return deliveryDirectionsFilterSelectItems;
    }

    public void setDeliveryDirectionsFilterSelectItems(SelectItem[] deliveryDirectionsFilterSelectItems) {
        this.deliveryDirectionsFilterSelectItems = deliveryDirectionsFilterSelectItems;
    }

    public String getEnteredPassword() {
        return enteredPassword;
    }

    public void setEnteredPassword(String enteredPassword) {
        this.enteredPassword = enteredPassword;
    }
}
