/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.PrintingHouse;
import com.griffinslogistics.exceptions.ExistingEntityException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgi
 */
public class PrintingHousesHelper {

    private static final Logger logger = Logger.getLogger(PrintingHousesHelper.class.getName());
    private static final String CLASS_NAME = PrintingHousesHelper.class.getSimpleName();

    private Session session;

    public PrintingHousesHelper() {

    }

    public List<PrintingHouse> getAllPrintingHouses() {
        logger.log(Level.SEVERE, "{0}: getAllPrintingHouses started", CLASS_NAME);

        List<PrintingHouse> resultList = new ArrayList<PrintingHouse>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            resultList = (ArrayList<PrintingHouse>) this.session.createCriteria(PrintingHouse.class).list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            PrintingHousesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAllPrintingHouses finished", CLASS_NAME);

        }

        Collections.reverse(resultList);

        return resultList;
    }

    public boolean addPrintingHouse(PrintingHouse newPrintingHouse) throws ExistingEntityException {
        logger.log(Level.SEVERE, "{0}: addPrintingHouse started", CLASS_NAME);

        boolean added = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(PrintingHouse.class);
            criteria.add(Restrictions.eq("name", newPrintingHouse.getName()).ignoreCase());
            int foundCount = criteria.list().size();
            if (foundCount > 0) {
                throw new ExistingEntityException("Вече съществува печатница с това име.");
            }
            this.session.saveOrUpdate(newPrintingHouse);
            transaction.commit();
            added = true;
        } catch (HibernateException e) {
            transaction.rollback();
            PrintingHousesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: addPrintingHouse finished", CLASS_NAME);
        }

        return added;
    }
}
