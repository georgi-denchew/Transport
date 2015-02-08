/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.AdditionalCost;
import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Transportation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Georgi
 */
public class AdditionalCostsHelper implements java.io.Serializable {

    private static final Logger logger = Logger.getLogger(AdditionalCostsHelper.class.getName());
    private static final String CLASS_NAME = AdditionalCostsHelper.class.getSimpleName();

    private Session session;

    public boolean updateAdditionalCost(AdditionalCost additionalCost) {
        logger.log(Level.SEVERE, "{0}: updateAdditionalCost started", CLASS_NAME);

        boolean successful = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(additionalCost);
            transaction.commit();
            successful = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: updateAdditionalCost finished", CLASS_NAME);
        }

        return successful;
    }

    public List<AdditionalCost> getAdditionalCostsByTransportation(Integer id) {
        logger.log(Level.SEVERE, "{0}: getAdditionalCostsByTransportation started", CLASS_NAME);

        List<AdditionalCost> result = new ArrayList<AdditionalCost>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Transportation transportation = (Transportation) this.session.get(Transportation.class, id);
            for (AdditionalCost additionalCost : transportation.getAdditionalCosts()) {
                result.add(additionalCost);
            }
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAdditionalCostsByTransportation finished", CLASS_NAME);
        }

        Collections.reverse(result);

        return result;
    }

}
