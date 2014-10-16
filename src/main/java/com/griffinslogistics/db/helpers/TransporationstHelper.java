/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Transportation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgi
 */
public class TransporationstHelper implements Serializable {

    private static final Logger logger = Logger.getLogger(TransporationstHelper.class.getName());

    private Session session;

    public TransporationstHelper() {

    }

    public boolean addTransportation(Transportation transportation) {

        boolean result = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(transportation);
            transaction.commit();

            result = true;

        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        return result;
    }

    public List<Transportation> getAllTransportations() {

        this.session = HibernateUtil.getSessionFactory().openSession();

        List<Transportation> allTransportations = new ArrayList<Transportation>();

        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Transportation.class);
            allTransportations = (List<Transportation>) criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        return allTransportations;

    }

    public boolean existsTransportation(Transportation transportation) {
        boolean existsTransportation = true;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        Transportation existingTransportation = null;
        try {

            Criteria criteria = session.createCriteria(Transportation.class);
            criteria.add(Restrictions.eq("weekNumber", transportation.getWeekNumber()));
            criteria.add(Restrictions.eq("year", transportation.getYear()));
            criteria.add(Restrictions.eq("startDate", transportation.getStartDate()));

            existingTransportation = (Transportation) criteria.uniqueResult();
            transaction.commit();

            if (existingTransportation == null) {
                existsTransportation = false;
            }

        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }
        
        return existsTransportation;
    }

    public List<Integer> getAllTransportationYears() {
        List<Integer> years = new ArrayList<Integer>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            years = (List<Integer>) this.session.
                    createQuery("SELECT DISTINCT t.year FROM Transportation t").list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        return years;
    }

    public List<Integer> getCreatedWeeksForYear(Integer year) {
        List<Integer> weeks = new ArrayList<Integer>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery("SELECT t.weekNumber FROM Transportation t where t.year = :year");
            query.setInteger("year", year);

            weeks = (List<Integer>) query.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        Collections.sort(weeks);
        Collections.reverse(weeks);
        return weeks;
    }

    public boolean deleteTransportation(Transportation transportation) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();
        
        boolean isDeleted = false;
        try {
//            transportation = (Transportation) this.session.merge(transportation);
            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());
            this.session.delete(transportation);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationstHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }
        
        return isDeleted;
    }
}
