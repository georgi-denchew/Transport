/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.Attachment;
import com.griffinslogistics.db.entities.Delivery;
import com.griffinslogistics.db.entities.Deliverydirection;
import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.exceptions.ConcurentUpdateException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.Stateless;
import javax.inject.Named;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgi Ivanov
 */
@Named(value = "deliveryHelper")
@Stateless
public class DeliveriesHelper implements DeliveryHelperLocal, Serializable {

    private static final Logger logger = Logger.getLogger(DeliveriesHelper.class.getName());

    private static final String QUERY_MAX_DELIVERY_NUMBER = "select max(delivery.deliveryNumber) from Delivery delivery where delivery.deliveryNumber like :queryParameter";
    private static final String CLASS_NAME = DeliveriesHelper.class.getSimpleName();

    private Session session;

    public DeliveriesHelper() {
    }

    @Override
    public List<Delivery> getAllDeliveries() {
        logger.log(Level.SEVERE, "{0}: getAllDeliveries started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        List<Delivery> result = new ArrayList<Delivery>();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Delivery.class);
            result = (List<Delivery>) criteria
//                    .setFetchMode("attachments", FetchMode.JOIN)
                    .setFetchMode("deliverydirection", FetchMode.JOIN)
                    .list();

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
            logger.log(Level.SEVERE, "{0}: getAllDeliveries finished", CLASS_NAME);

        }

        Collections.reverse(result);
        return result;
    }

    @Override
    public boolean isEmpty(Delivery delivery) {
        logger.log(Level.SEVERE, "{0}: isEmpty started", CLASS_NAME);

        boolean isEmpty = true;

        Field[] fields = Delivery.class.getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            try {
                Object value = field.get(delivery);

                if (value != null) {
                    if (value instanceof Collection && ((Collection) value).isEmpty()) {
                        continue;
                    }

                    isEmpty = false;
                    break;
                }
            } catch (IllegalArgumentException ex) {
                DeliveriesHelper.logger.log(Level.SEVERE, ex.getMessage(), delivery);
            } catch (IllegalAccessException ex) {
                Logger.getLogger(DeliveriesHelper.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        logger.log(Level.SEVERE, "{0}: isEmpty finished", CLASS_NAME);

        return isEmpty;
    }

    @Override
    public boolean updateDelivery(Delivery delivery) throws ConcurentUpdateException {
        logger.log(Level.SEVERE, "{0}: updateDelivery started", CLASS_NAME);

        boolean updated = false;
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(delivery);
            transaction.commit();

            updated = true;
        } catch (StaleObjectStateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), delivery);
            throw new ConcurentUpdateException("Доставката е вече променена. Моля опитайте отново.");
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), delivery);
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: updateDelivery finished", CLASS_NAME);

        }
        return updated;
    }

    @Override
    public Delivery getDeliveryById(int id) {
        logger.log(Level.SEVERE, "{0}: getDeliveryById started", CLASS_NAME);

        Delivery delivery = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            Criteria criteria = session.createCriteria(Delivery.class);
            criteria.add(Restrictions.eq("id", id));
            delivery = (Delivery) criteria.uniqueResult();
            transaction.commit();

            Deliverydirection direction = delivery.getDeliverydirection();
            int a = 1;
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), delivery);
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getDeliveryById finished", CLASS_NAME);

        }
        return delivery;
    }

    @Override
    public boolean deleteDelivery(Delivery delivery) {
        logger.log(Level.SEVERE, "{0}: deleteDelivery started", CLASS_NAME);

        boolean isDeleted = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();

        try {
            this.session.delete(delivery);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), delivery);
            throw new HibernateException(e);
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: deleteDelivery finished", CLASS_NAME);
        }

        return isDeleted;
    }

    @Override
    public void addAttachment(Delivery selectedDelivery, Attachment newAttachment) {
        logger.log(Level.SEVERE, "{0}: addAttachment started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        try {
            newAttachment.setDelivery(selectedDelivery);
            newAttachment.setId(-1);
            this.session.save(newAttachment);

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), new Object[]{selectedDelivery, newAttachment});
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: addAttachment finished", CLASS_NAME);

        }
    }

    @Override
    public List<Attachment> getAttachments(int deliveryId) {
        logger.log(Level.SEVERE, "{0}: getAttachments started", CLASS_NAME);

        List<Attachment> resultAttachments = new ArrayList<Attachment>();
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = session.beginTransaction();
        
        Delivery delivery = null;
        
        try {
            delivery = (Delivery) this.session.get(Delivery.class, deliveryId);
            transaction.commit();
            resultAttachments = new ArrayList<Attachment>(delivery.getAttachments());

        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage(), delivery);
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAttachments finished", CLASS_NAME);
        }
        return resultAttachments;
    }

    @Override
    public List<Deliverydirection> getAllDeliveryDirections() {
        logger.log(Level.SEVERE, "{0}: getAllDeliveryDirections started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();

        List<Deliverydirection> resultDeliverydirections = new ArrayList<Deliverydirection>();

        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Deliverydirection.class);
            resultDeliverydirections = (List<Deliverydirection>) criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAllDeliveryDirections finished", CLASS_NAME);
        }

        return resultDeliverydirections;
    }

    @Override
    public Deliverydirection getDeliveryDirectionById(int id) {
        logger.log(Level.SEVERE, "{0}: getDeliveryDirectionById started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();

        Deliverydirection resultDeliverydirection = null;

        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Deliverydirection.class);
            criteria.add(Restrictions.eq("id", id));
            resultDeliverydirection = (Deliverydirection) criteria.uniqueResult();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            DeliveriesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getDeliveryDirectionById finished", CLASS_NAME);

        }

        return resultDeliverydirection;
    }

    public String biggestDeliveryNumber(int week, int year) {
        logger.log(Level.SEVERE, "{0}: biggestDeliveryNumber started", CLASS_NAME);

        String biggestDeliveryNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery(QUERY_MAX_DELIVERY_NUMBER);
            String queryParameter = String.format("G-%s-%%-%s", year, week);
            query.setParameter("queryParameter", queryParameter);
            biggestDeliveryNumber = (String) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: biggestDeliveryNumber finished", CLASS_NAME);
        }

        return biggestDeliveryNumber;
    }
}
