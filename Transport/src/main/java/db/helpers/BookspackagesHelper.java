/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.helpers;

import db.Bookspackage;
import db.HibernateUtil;
import db.Transportation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Georgi
 */
public class BookspackagesHelper implements Serializable {

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
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        Collections.reverse(resultList);
        
        return resultList;
    }

    public boolean updateBookspackage(Bookspackage bookspackage) {
        boolean updated = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(bookspackage);
            transaction.commit();
            updated = true;
        } catch (HibernateException e) {
            transaction.rollback();
            BookspackagesHelper.logger.log(Level.SEVERE, e.getMessage());
        }

        return updated;
    }

    public String getBiggestBookspackageNumberForTransportation(Transportation transportation) {
        String biggestBookspackageNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery("select max(b.packageNumber) from Bookspackage b where b.transportation = :transportation");
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
        
        return isDeleted;
    }

}
