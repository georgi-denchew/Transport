/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package db.helpers;

import db.Attachment;
import db.Delivery;
import db.Deliverydirection;
import exceptions.ConcurentUpdateException;
import java.util.List;
import javax.ejb.Local;
import javax.ejb.Remote;

/**
 *
 * @author Georgi
 */
@Local
public interface DeliveryHelperLocal {
    public List<Delivery> getAllDeliveries();
    public boolean isEmpty(Delivery delivery);

    public boolean updateDelivery (Delivery delivery) throws ConcurentUpdateException;
    public Delivery getDeliveryById(int id);
    public boolean deleteDelivery(Delivery delivery);
    public void addAttachment(Delivery selectedDelivery, Attachment newAttachment);
    public List<Attachment> getAttachments(Delivery delivery);
    public List<Deliverydirection> getAllDeliveryDirections();
    public Deliverydirection getDeliveryDirectionById(int id);
}
