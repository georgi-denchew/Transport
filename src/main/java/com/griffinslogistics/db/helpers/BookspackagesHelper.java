/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Bookspackage;
import com.griffinslogistics.db.entities.BookspackageHistory;
import com.griffinslogistics.db.entities.Box;
import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Transportation;
import com.griffinslogistics.db.entities.TruckGroup;
import com.griffinslogistics.exceptions.ConcurentUpdateException;
import com.griffinslogistics.models.BookspackageCMRModel;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.persistence.internal.oxm.schema.model.Restriction;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.StaleObjectStateException;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author Georgi
 */
public class BookspackagesHelper implements Serializable {

    private static final String QUERY_TOTAL_WEIGHT_FOR_ALL_BOOKSPACKAGES = "select bookspackage.packageNumber as packageNumber, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight, sum(box.boxesCount) as totalBoxesCount "
            + "from Book book inner join book.boxes as box "
            + "inner join book.bookspackage as bookspackage "
            + "where book.transportation = :transportation "
            + "group by book.bookspackage";

    private static final String QUERY_TOTAL_WEIGHT_FOR_BOOKSPACKAGE = "select bookspackage.packageNumber as packageNumber, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight, sum(box.boxesCount) as totalBoxesCount "
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
          
            //lazy invoke 
//            transportationForBookspackage = (Transportation) this.session.merge(transportationForBookspackage);
            transportationForBookspackage = (Transportation) this.session.get(Transportation.class, transportationForBookspackage.getId());
            transportationForBookspackage.getBookspackages().size();
            List<Bookspackage> bookspackages = transportationForBookspackage.getBookspackages();
            transaction.commit();

            for (Bookspackage bookspackage : bookspackages) {
                int totalBooksCount = 0;
                double totalBooksWeight = 0;
                int totalOrderedBooksCount = 0;
                double totalOrderedBooksWeight = 0;

                for (Book book : bookspackage.getBooks()) {
                    totalOrderedBooksCount += book.getCount();
                    totalOrderedBooksWeight += book.getWeight();

                    for (Box box : (Set<Box>) book.getBoxes()) {
                        int boxesCount = box.getBoxesCount();
                        int booksCount = box.getBooksCount();
                        
                        totalBooksCount += boxesCount * booksCount;
                        totalBooksWeight += boxesCount * booksCount * book.getWeightPerBook();
                    }
                }
                
                TruckGroup truckGroup = bookspackage.getTruckGroup();
                if (truckGroup != null) {
                    truckGroup.getName();
                } 
//                else {
//                    bookspackage.setTruckGroup(new TruckGroup());
//                    bookspackage.getTruckGroup().setId(0);
//                }
                
                totalBooksWeight = Math.round(totalBooksWeight);
                totalOrderedBooksWeight = Math.round(totalOrderedBooksWeight);
                bookspackage.setTotalBooksCount(totalBooksCount);
                bookspackage.setTotalBooksWeight(totalBooksWeight);
                bookspackage.setTotalOrderedBooksCount(totalOrderedBooksCount);
                bookspackage.setTotalOrderedBooksWeight(totalOrderedBooksWeight);
                resultList.add(bookspackage);
            }

        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        Collections.reverse(resultList);

        return resultList;
    }

    public Map<String, Double> totalWeightsForTransportation(Transportation transportation) {
        Map<String, Double> result = new HashMap<String, Double>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        String transportationString = String.format("Транспорт %s/%s", transportation.getWeekNumber(), transportation.getYear());

        try {
//            transportation = (Transportation) this.session.merge(transportation);
            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());
            List<Bookspackage> bookspackages = transportation.getBookspackages();

            for (Bookspackage bookspackage : bookspackages) {

                double totalWeightForTruckGroup = 0;

                for (Book book : bookspackage.getBooks()) {
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
        }finally{
        this.session.close();
        }

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
        }finally{
        this.session.close();
        }

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
        }finally{
        this.session.close();
        }

        return result;
    }

    public boolean updateBookspackage(Bookspackage bookspackage) throws ConcurentUpdateException {
        boolean updated = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            boolean hasChanged = true;

            if (bookspackage.getId() != null) {
                Bookspackage existing = (Bookspackage) this.session.get(Bookspackage.class, bookspackage.getId());

                boolean haveEqualProperites = this.haveEqualProperties(existing, bookspackage);
                hasChanged = !haveEqualProperites;
                
//                this.session.evict(existing);
//                this.session.saveOrUpdate(bookspackage);
                
                this.session.merge(bookspackage);
            } else {
                this.session.saveOrUpdate(bookspackage);
            }

            if (hasChanged) {
                //Save history
                BookspackageHistory history = new BookspackageHistory();
                history.setBookspackage(bookspackage);
                history.setClient(bookspackage.getClient());
                history.setCountry(bookspackage.getCountry());
                history.setDeliveryAddress(bookspackage.getDeliveryAddress());
                history.setDeliveryDate(bookspackage.getDeliveryDate());
                history.setEmail(bookspackage.getEmail());
                history.setMerchant(bookspackage.getMerchant());
                history.setPhoneNumber(bookspackage.getPhoneNumber());
                history.setPostalCode(bookspackage.getPostalCode());
                history.setRemarks(bookspackage.getRemarks());
                history.setPrintDeliveryDay(bookspackage.getPrintDeliveryDay());

                if (bookspackage.getTruckGroup() != null && bookspackage.getTruckGroup().getId() > 0) {
                    history.setTruckGroupName(bookspackage.getTruckGroup().getName());
                }

                Date currentDate = Calendar.getInstance().getTime();
                history.setLastModification(currentDate);

                this.session.saveOrUpdate(history);
            }

            transaction.commit();
            updated = true;
            bookspackage.setVersion(bookspackage.getVersion() + 1);
        } catch (StaleObjectStateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
            throw new ConcurentUpdateException("Пратката е вече променена. Моля опитайте отново.");
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
            throw e;
        }finally{
        this.session.close();
        }
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
        }finally{
        this.session.close();
        }

        return biggestBookspackageNumber;
    }

    public boolean deleteBookspackage(Bookspackage selectedBookspackage) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean isDeleted = false;

        try {
//            selectedBookspackage = (Bookspackage) this.session.merge(selectedBookspackage);
            selectedBookspackage = (Bookspackage) this.session.get(Bookspackage.class, selectedBookspackage.getId());
            this.session.delete(selectedBookspackage);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }
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
        }finally{
        this.session.close();
        }

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
        }finally{
        this.session.close();
        }

        return result;
    }

    public List<BookspackageHistory> getBookpackagesHistory(Bookspackage bookspackage) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();
        List<BookspackageHistory> result = null;

        try {
            Criteria criteria = this.session.createCriteria(BookspackageHistory.class);
            criteria.add(Restrictions.eq("bookspackage", bookspackage));
            result = (List<BookspackageHistory>) criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }

        return result;
    }

    private boolean haveEqualProperties(Bookspackage existing, Bookspackage bookspackage) {
        boolean haveEqualProperties = true;

        haveEqualProperties = haveEqualProperties && existing.getClient().equals(bookspackage.getClient());
        haveEqualProperties = haveEqualProperties && existing.getCountry().equals(bookspackage.getCountry());
        haveEqualProperties = haveEqualProperties && existing.getDeliveryAddress().equals(bookspackage.getDeliveryAddress());
        haveEqualProperties = haveEqualProperties && existing.getEmail().equals(bookspackage.getEmail());
        haveEqualProperties = haveEqualProperties && existing.getMerchant().equals(bookspackage.getMerchant());
        haveEqualProperties = haveEqualProperties && existing.getPhoneNumber().equals(bookspackage.getPhoneNumber());
        haveEqualProperties = haveEqualProperties && existing.getPostalCode().equals(bookspackage.getPostalCode());
        haveEqualProperties = haveEqualProperties && existing.getRemarks().equals(bookspackage.getRemarks());
        haveEqualProperties = haveEqualProperties && existing.getPrintDeliveryDay().equals(bookspackage.getPrintDeliveryDay());

        TruckGroup existingTruckGroup = existing.getTruckGroup();
        TruckGroup bookspackageTruckGroup = bookspackage.getTruckGroup();

        if (existingTruckGroup != null && bookspackageTruckGroup != null) {
            haveEqualProperties = haveEqualProperties && existingTruckGroup.getId().equals(bookspackageTruckGroup.getId());
        } else if (existingTruckGroup == null && bookspackageTruckGroup == null) {
            haveEqualProperties = haveEqualProperties && true;
        } else {
            haveEqualProperties = false;
        }

        Date existingDeliveryDate = existing.getDeliveryDate();
        Date bookspackageDeliveryDate = bookspackage.getDeliveryDate();

        if (existingDeliveryDate != null && bookspackageDeliveryDate != null) {
            haveEqualProperties = haveEqualProperties && existingDeliveryDate.equals(bookspackageDeliveryDate);
        } else if (existingDeliveryDate == null && bookspackageDeliveryDate == null) {
            haveEqualProperties = haveEqualProperties && true;
        } else {
            haveEqualProperties = false;
        }

        return haveEqualProperties;
    }
}
