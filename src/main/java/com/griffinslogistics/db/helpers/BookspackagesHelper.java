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
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
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

    private static final String CLASS_NAME = BookspackagesHelper.class.getSimpleName();

    private static final String QUERY_TOTAL_WEIGHT_FOR_ALL_BOOKSPACKAGES = "select bookspackage.packageNumber as packageNumber, bookspackage.deliveryAddress as deliveryAddress, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight, sum(box.boxesCount) as totalBoxesCount "
            + "from Book book inner join book.boxes as box "
            + "inner join book.bookspackage as bookspackage "
            + "where book.transportation = :transportation "
            + "group by book.bookspackage";

    private static final String QUERY_TOTAL_WEIGHT_FOR_BOOKSPACKAGE = "select bookspackage.packageNumber as packageNumber, bookspackage.deliveryAddress as deliveryAddress, sum(book.weightPerBook * box.booksCount * box.boxesCount) as totalWeight, sum(box.boxesCount) as totalBoxesCount "
            + "from Book book inner join book.boxes as box "
            + "inner join book.bookspackage as bookspackage "
            + "where book.bookspackage = :bookspackage ";

//    private static final String QUERY_BIGGEST_BOOKSPACKAGE_NUMBER_FOR_TRANSPORTATION = "select max(b.packageNumber) from Bookspackage b where b.transportation = :transportation";
    private static final String QUERY_BIGGEST_BOOKSPACKAGE_NUMBER_FOR_TRANSPORTATION = "select b.packageNumber from Bookspackage b where b.transportation = :transportation";

    private static final Logger logger = Logger.getLogger(BookspackagesHelper.class.getName());
    private Session session;

    public List<Bookspackage> getBookspackagesByTransport(Transportation transportationForBookspackage) {
        logger.log(Level.SEVERE, "{0}: getBookspackagesByTransport started", CLASS_NAME);

        List<Bookspackage> resultList = new ArrayList<Bookspackage>();
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Transportation.class);
            criteria.add(Restrictions.eq("id", transportationForBookspackage.getId()));
            criteria.setFetchMode("bookspackages", FetchMode.JOIN);
            criteria.setFetchMode("bookspackages.truckGroup", FetchMode.JOIN);
            criteria.setFetchMode("bookspackages.books", FetchMode.JOIN);
            criteria.setFetchMode("bookspackages.books.boxes", FetchMode.JOIN);
            transportationForBookspackage = (Transportation) criteria.uniqueResult();
//            transportationForBookspackage = (Transportation) this.session.get(Transportation.class, transportationForBookspackage.getId());
//            transportationForBookspackage.getBookspackages().size();
            transaction.commit();
            Set<Bookspackage> bookspackages = transportationForBookspackage.getBookspackages();

            for (Bookspackage bookspackage : bookspackages) {
                int totalBooksCount = 0;
                double totalBooksWeight = 0;
                int totalOrderedBooksCount = 0;
                double totalOrderedBooksWeight = 0;

                for (Book book : bookspackage.getBooks()) {

                    if (book.isDiscarded()) {
                        bookspackage.setHasDiscardedBooks(true);
                    }

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

                totalBooksWeight = Math.round(totalBooksWeight);
                totalOrderedBooksWeight = Math.round(totalOrderedBooksWeight);
                bookspackage.setTotalBooksCount(totalBooksCount);
                bookspackage.setTotalBooksWeight(totalBooksWeight);
                bookspackage.setTotalOrderedBooksCount(totalOrderedBooksCount);
                bookspackage.setTotalOrderedBooksWeight(totalOrderedBooksWeight);
                resultList.add(bookspackage);

            }
        } catch (HibernateException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        } finally {
            logger.log(Level.SEVERE, "{0}: getBookspackagesByTransport finished", CLASS_NAME);

            this.session.close();
        }
        Collections.reverse(resultList);

        return resultList;
    }

    public List<Bookspackage> getBookspackagesByTransportAndBookFilter(Transportation transportation, Book bookFilter) {
        logger.log(Level.SEVERE, "{0}: getBookspackagesByTransportAndBookFilter started", CLASS_NAME);

        List<Bookspackage> resultList = new ArrayList<Bookspackage>();
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {

            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());
            transportation.getBookspackages().size();
            Set<Bookspackage> bookspackages = transportation.getBookspackages();
            transaction.commit();

            for (Bookspackage bookspackage : bookspackages) {
                int totalBooksCount = 0;
                double totalBooksWeight = 0;
                int totalOrderedBooksCount = 0;
                double totalOrderedBooksWeight = 0;

                boolean hasBookFilter = false;

                for (Book book : bookspackage.getBooks()) {

                    if (book.isDiscarded()) {
                        bookspackage.setHasDiscardedBooks(true);
                    }

                    if (book.getTitle().trim().equalsIgnoreCase(bookFilter.getTitle().trim())) {
                        hasBookFilter = true;
                    }

                    totalOrderedBooksCount += book.getCount();
                    totalOrderedBooksWeight += book.getWeight();

                    for (Box box : (Set<Box>) book.getBoxes()) {
                        int boxesCount = box.getBoxesCount();
                        int booksCount = box.getBooksCount();

                        totalBooksCount += boxesCount * booksCount;
                        totalBooksWeight += boxesCount * booksCount * book.getWeightPerBook();
                    }
                }

                if (!hasBookFilter) {
                    continue;
                }

                TruckGroup truckGroup = bookspackage.getTruckGroup();
                if (truckGroup != null) {
                    truckGroup.getName();
                }

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getBookspackagesByTransportAndBookFilter finished", CLASS_NAME);

        }

        Collections.reverse(resultList);

        return resultList;
    }

    public Map<String, Double> totalWeightsForTransportation(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: totalWeightsForTransportation started", CLASS_NAME);

        Map<String, Double> result = new TreeMap<String, Double>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        String transportationString = String.format("Транспорт‚ %s/%s", transportation.getWeekNumber(), transportation.getYear());

        try {
            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());
            Set<Bookspackage> bookspackages = transportation.getBookspackages();

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: totalWeightsForTransportation finished", CLASS_NAME);

        }

        return result;
    }

    public List<BookspackageCMRModel> totalWeightForBookspackages(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: totalWeightForBookspackages started", CLASS_NAME);

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: totalWeightForBookspackages finished", CLASS_NAME);

        }

        return result;
    }

    public double totalWeightForTruckGroup(Transportation transportation, TruckGroup truckGroup) {
        logger.log(Level.SEVERE, "{0}: totalWeightForTruckGroup started", CLASS_NAME);

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: totalWeightForTruckGroup finished", CLASS_NAME);
        }

        return result;
    }

    public boolean updateBookspackage(Bookspackage bookspackage) throws ConcurentUpdateException {
        logger.log(Level.SEVERE, "{0}: updateBookspackage started", CLASS_NAME);

        boolean updated = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            boolean hasChanged = true;

            if (bookspackage.getId() != null) {
                Bookspackage existing = (Bookspackage) this.session.get(Bookspackage.class, bookspackage.getId());

                boolean haveEqualProperites = false;
                if (existing != null) {
                    haveEqualProperites = existing.haveEqualProperties(bookspackage);
                }
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
                history.setRemarksSales(bookspackage.getRemarksSales());
                history.setRemarksLogistics(bookspackage.getRemarksLogistics());
                history.setPrintDeliveryDay(bookspackage.getPrintDeliveryDay());
                history.setPricePerKilogram(bookspackage.getPricePerKilogram());
                history.setLoadingDay(bookspackage.getLoadingDay());
                if (bookspackage.getTruckGroup() != null && bookspackage.getTruckGroup().getId() > 0) {
                    history.setTruckGroupName(bookspackage.getTruckGroup().getName());
                }

                Date currentDate = Calendar.getInstance().getTime();
                history.setLastModification(currentDate);

                this.session.saveOrUpdate(history);
            }

            transaction.commit();
            updated = true;
            bookspackage.setVersion(((Bookspackage) this.session.get(Bookspackage.class, bookspackage.getId())).getVersion());
//            bookspackage.setVersion(bookspackage.getVersion() + 1);

        } catch (StaleObjectStateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
            throw new ConcurentUpdateException("РџСЂР°С‚РєР°С‚Р° Рµ РІРµС‡Рµ РїСЂРѕРјРµРЅРµРЅР°. РњРѕР»СЏ РѕРїРёС‚Р°Р№С‚Рµ РѕС‚РЅРѕРІРѕ.");
        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
            throw e;
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: updateBookspackage finished", CLASS_NAME);
        }
        return updated;
    }

    public String getBiggestBookspackageNumberForTransportation(Transportation transportation) {
        logger.log(Level.SEVERE, "{0}: getBiggestBookspackageNumberForTransportation started", CLASS_NAME);

        String biggestBookspackageNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery(QUERY_BIGGEST_BOOKSPACKAGE_NUMBER_FOR_TRANSPORTATION);
            query.setParameter("transportation", transportation);
            List<String> numberStrings = (List<String>) query.list();

            int maxNumber = 0;
            for (String string : numberStrings) {
                int firstDashIndex = string.indexOf("-");

                String substring = string.substring(0, firstDashIndex);
                int currentNumber = Integer.parseInt(substring);

                if (maxNumber < currentNumber) {
                    maxNumber = currentNumber;
                    biggestBookspackageNumber = string;
                }
            }
            numberStrings.size();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getBiggestBookspackageNumberForTransportation finished", CLASS_NAME);

        }

        return biggestBookspackageNumber;
    }

    public boolean deleteBookspackage(Bookspackage selectedBookspackage) {
        logger.log(Level.SEVERE, "{0}: deleteBookspackage started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean isDeleted = false;

        try {
            selectedBookspackage = (Bookspackage) this.session.get(Bookspackage.class, selectedBookspackage.getId());
            this.session.delete(selectedBookspackage);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: deleteBookspackage finished", CLASS_NAME);

        }
        return isDeleted;
    }

    public List<TruckGroup> getAllTruckGroups() {
        logger.log(Level.SEVERE, "{0}: getAllTruckGroups started", CLASS_NAME);

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getAllTruckGroups finished", CLASS_NAME);
        }

        return resultList;
    }

    public BookspackageCMRModel totalWeightForBookspackage(Bookspackage bookspackage) {
        logger.log(Level.SEVERE, "{0}: totalWeightForBookspackage started", CLASS_NAME);

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: totalWeightForBookspackage finished", CLASS_NAME);
        }

        return result;
    }

    public List<BookspackageHistory> getBookpackagesHistory(Bookspackage bookspackage) {
        logger.log(Level.SEVERE, "{0}: getBookpackagesHistory started", CLASS_NAME);

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
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getBookpackagesHistory finished", CLASS_NAME);
        }

        return result;
    }

    public Bookspackage getBookspackageById(Integer bookspackageId) {
        logger.log(Level.SEVERE, "{0}: getBookspackageById started", CLASS_NAME);

        Bookspackage result = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            result = (Bookspackage) this.session.get(Bookspackage.class, bookspackageId);

            //lazy
            result.getTransportation().getId();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getBookspackageById finished", CLASS_NAME);

        }

        return result;
    }
}
