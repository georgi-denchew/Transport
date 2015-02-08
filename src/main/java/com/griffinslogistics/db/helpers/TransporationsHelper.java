/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Bookspackage;
import com.griffinslogistics.db.entities.Box;
import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.entities.TruckGroup;
import com.griffinslogistics.models.TruckGroupTotalsModel;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
public class TransporationsHelper implements Serializable {

    private static final Logger logger = Logger.getLogger(TransporationsHelper.class.getName());
    private static final String CLASS_NAME = TransporationsHelper.class.getSimpleName();

    private Session session;

    public TransporationsHelper() {

    }

    public boolean addTransportation(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: addTransportation started", CLASS_NAME);

        boolean result = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(transportation);
            transaction.commit();

            result = true;

        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: addTransportation finished", CLASS_NAME);
        }

        return result;
    }

    public List<Transportation> getAllTransportations() {
        logger.log(Level.SEVERE, "{0}: getAllTransportations started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();

        List<Transportation> allTransportations = new ArrayList<Transportation>();

        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Transportation.class);
            allTransportations = (List<Transportation>) criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAllTransportations finished", CLASS_NAME);

        }

        return allTransportations;

    }

    public boolean existsTransportation(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: existsTransportation started", CLASS_NAME);

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
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: existsTransportation finished", CLASS_NAME);
        }

        return existsTransportation;
    }

    public List<Integer> getAllTransportationYears() {
        logger.log(Level.SEVERE, "{0}: getAllTransportationYears started", CLASS_NAME);

        List<Integer> years = new ArrayList<Integer>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            years = (List<Integer>) this.session.
                    createQuery("SELECT DISTINCT t.year FROM Transportation t").list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAllTransportationYears finished", CLASS_NAME);

        }

        return years;
    }

    public List<Integer> getCreatedWeeksForYear(Integer year) {
        logger.log(Level.SEVERE, "{0}: getCreatedWeeksForYear started", CLASS_NAME);

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
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

        Collections.sort(weeks);
        Collections.reverse(weeks);

        logger.log(Level.SEVERE, "{0}: getCreatedWeeksForYear started", CLASS_NAME);
        return weeks;
    }

    public boolean deleteTransportation(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: deleteTransportation started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean isDeleted = false;
        try {
            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());
            this.session.delete(transportation);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: deleteTransportation finished", CLASS_NAME);
        }

        return isDeleted;
    }

    public Transportation getTransportationById(Integer transportationId) {
        logger.log(Level.SEVERE, "{0}: getTransportationById started", CLASS_NAME);

        Transportation result = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            result = (Transportation) this.session.get(Transportation.class, transportationId);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getTransportationById finished", CLASS_NAME);
        }
        return result;
    }

    public Transportation getTransportationByBookspackage(Bookspackage bookspackage) {
        logger.log(Level.SEVERE, "{0}: getTransportationByBookspackage started", CLASS_NAME);

        Transportation result = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            result = bookspackage.getTransportation();
            result.getWeekNumber();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            TransporationsHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getTransportationByBookspackage finished", CLASS_NAME);

        }

        return result;
    }

    public Map<String, TruckGroupTotalsModel> getTruckGroupTotalsForTransportation(Integer transportationId) {
        logger.log(Level.SEVERE, "{0}: getTruckGroupTotalsForTransportation started", CLASS_NAME);

        Map<String, TruckGroupTotalsModel> resultModels = new HashMap<String, TruckGroupTotalsModel>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Transportation transportation = (Transportation) this.session.get(Transportation.class, transportationId);
            Set<Bookspackage> bookspackages = transportation.getBookspackages();

            for (Bookspackage bookspackage : bookspackages) {

                double totalWeightForTruckGroup = 0;
                double totalOrderedBooksWeight = 0;

                for (Book book : bookspackage.getBooks()) {
                    double weightPerBook = book.getWeightPerBook();

                    totalOrderedBooksWeight += book.getWeight();

                    for (Box box : (Set<Box>) book.getBoxes()) {
                        totalWeightForTruckGroup += box.getBooksCount() * box.getBoxesCount() * weightPerBook;
                    }
                }

                if (totalWeightForTruckGroup > 0) {

                    TruckGroup truckGroup = bookspackage.getTruckGroup();

                    if (truckGroup != null) {

                        TruckGroupTotalsModel model;

                        if (resultModels.containsKey(truckGroup.getName())) {
                            model = resultModels.get(truckGroup.getName());
                        } else {
                            model = new TruckGroupTotalsModel(truckGroup.getId(), truckGroup.getName(), 0, 0, 0d);
                        }

                        model.setPackagesCount(model.getPackagesCount() + 1);
                        model.setArrivedWeight(model.getArrivedWeight() + (int) totalWeightForTruckGroup);
                        double newTotalPrice = model.getTotalPrice() + (totalOrderedBooksWeight * bookspackage.getPricePerKilogram());
                        model.setTotalPrice(newTotalPrice);

                        resultModels.put(truckGroup.getName(), model);
                    }
                }
            }

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getTruckGroupTotalsForTransportation finished", CLASS_NAME);
        }

        return resultModels;
    }
}
