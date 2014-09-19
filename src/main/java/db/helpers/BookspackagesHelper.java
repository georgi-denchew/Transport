/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.helpers;

import db.Book;
import db.Bookspackage;
import db.Box;
import db.HibernateUtil;
import db.Transportation;
import db.TruckGroup;
import exceptions.ConcurentUpdateException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.BookspackageCMRModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.transform.Transformers;

/**
 *
 * @author Georgi
 */
public class BookspackagesHelper implements Serializable {

    private static final String QUERY_TOTAL_WEIGHT_FOR_ALL_BOOKSPACKAGES = "select bookspackage.packageNumber as packageNumber, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight "
            + "from Book book inner join book.boxes as box "
            + "inner join book.bookspackage as bookspackage "
            + "where book.transportation = :transportation "
            + "group by book.bookspackage";

    private static final String QUERY_TOTAL_WEIGHT_FOR_BOOKSPACKAGE = "select bookspackage.packageNumber as packageNumber, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight "
            + "from Book book inner join book.boxes as box "
            + "inner join book.bookspackage as bookspackage "
            + "where book.bookspackage = :bookspackage ";

    private static final String QUERY_BIGGEST_BOOKSPACKAGE_NUMBER_FOR_TRANSPORTATION = "select max(b.packageNumber) from Bookspackage b where b.transportation = :transportation";

    private static final Logger logger = Logger.getLogger(BookspackagesHelper.class.getName());
    private Session session;

    public List<Bookspackage> getBookspackagesByTransport(Transportation transportationForBookspackage) {
        List<Bookspackage> resultList = new ArrayList<Bookspackage>();
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            transportationForBookspackage = (Transportation) this.session.merge(transportationForBookspackage);

            //lazy invoke 
            transportationForBookspackage.getBookspackages().size();
            resultList = transportationForBookspackage.getBookspackages();

            for (Bookspackage bookspackage : resultList) {
                TruckGroup truckGroup = bookspackage.getTruckGroup();
                if (truckGroup != null) {
                    truckGroup.getName();
                } else {
                    bookspackage.setTruckGroup(new TruckGroup());
                }
            }

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        Collections.reverse(resultList);

        return resultList;
    }

    public Map<String, Double> totalWeightsForTransportation(Transportation transportation) {
        Map<String, Double> result = new HashMap<String, Double>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        String transportationString = String.format("Транспорт %s/%s", transportation.getWeekNumber(), transportation.getYear());

        try {
            transportation = (Transportation) this.session.merge(transportation);
            List<Bookspackage> bookspackages = transportation.getBookspackages();

            for (Bookspackage bookspackage : bookspackages) {

                double totalWeightForTruckGroup = 0;

                for (Book book : (Set<Book>) bookspackage.getBooks()) {
                    double weightPerBook = book.getWeightPerBook();

                    for (Box box : (Set<Box>) book.getBoxes()) {
                        totalWeightForTruckGroup += box.getBooksCount() * box.getBoxesCount() * weightPerBook;
                    }
                }
                if (totalWeightForTruckGroup > 0) {
                    double totalWeightForTransport = totalWeightForTruckGroup;

                    if (result.containsKey(transportationString)) {
                        totalWeightForTransport += result.get(transportationString);
                    }
                    
                    result.put(transportationString, totalWeightForTransport);
                    
                    TruckGroup truckGroup = bookspackage.getTruckGroup();
                    
                    if (truckGroup != null) {
                        String truckGroupName = bookspackage.getTruckGroup().getName();

                        if (result.containsKey(truckGroupName)) {
                            totalWeightForTruckGroup += result.get(truckGroupName);
                        }

                        result.put(truckGroupName, totalWeightForTruckGroup);
                    }
                }
            }

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return result;
    }

    public List<BookspackageCMRModel> totalWeightForBookspackages(Transportation transportation) {
        List<BookspackageCMRModel> result = new ArrayList<BookspackageCMRModel>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery(QUERY_TOTAL_WEIGHT_FOR_ALL_BOOKSPACKAGES);
            query.setResultTransformer(Transformers.aliasToBean(BookspackageCMRModel.class));
            query.setParameter("transportation", transportation);
            result = (List<BookspackageCMRModel>) query.list();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return result;
    }

    public double totalWeightForTruckGroup(Transportation transportation, TruckGroup truckGroup) {
        double result = 0;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery("select sum(book.weightPerBook * box.booksCount * box.boxesCoun) "
                    + "from Bookspackage bookspackage inner join bookspackage.books as book "
                    + "inner book.boxes as box "
                    + "where book.transportation = :transportation and bookspackage.truckGroup = :truckGroup");
            query.setParameter("transportation", transportation);
            query.setParameter("truckGroup", truckGroup);
            result = (Double) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return result;
    }

    public boolean updateBookspackage(Bookspackage bookspackage) throws ConcurentUpdateException {
        boolean updated = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(bookspackage);
            transaction.commit();
            updated = true;
        } catch (StaleObjectStateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
            throw new ConcurentUpdateException("Пратката е вече променена. Моля опитайте отново.");
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();
        return updated;
    }

    public String getBiggestBookspackageNumberForTransportation(Transportation transportation) {
        String biggestBookspackageNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery(QUERY_BIGGEST_BOOKSPACKAGE_NUMBER_FOR_TRANSPORTATION);
            query.setParameter("transportation", transportation);
            biggestBookspackageNumber = (String) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return biggestBookspackageNumber;
    }

    public boolean deleteBookspackage(Bookspackage selectedBookspackage) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean isDeleted = false;

        try {
            selectedBookspackage = (Bookspackage) this.session.merge(selectedBookspackage);
            this.session.delete(selectedBookspackage);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();
        return isDeleted;
    }

    public List<TruckGroup> getAllTruckGroups() {
        List<TruckGroup> resultList = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(TruckGroup.class);
            resultList = criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return resultList;
    }

    public BookspackageCMRModel totalWeightForBookspackage(Bookspackage bookspackage) {
        BookspackageCMRModel result = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery(QUERY_TOTAL_WEIGHT_FOR_BOOKSPACKAGE);
            query.setResultTransformer(Transformers.aliasToBean(BookspackageCMRModel.class));
            query.setParameter("bookspackage", bookspackage);
            result = (BookspackageCMRModel) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return result;
    }

}
