/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package db.helpers;

import db.Book;
import db.Bookspackage;
import db.HibernateUtil;
import db.Transportation;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import models.BookExtendedModel;
import models.BookModel;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

/**
 *
 * @author Georgi
 */
public class BooksHelper implements Serializable {

    private static final Logger logger = Logger.getLogger(BooksHelper.class.getName());

    private Session session;

    public BooksHelper() {
    }

    public List<Book> getBooksByBookspackage(Bookspackage bookspackage) {
        List<Book> resultList = new ArrayList<Book>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Book.class);
            criteria.add(Restrictions.eq("bookspackage", bookspackage));
            resultList = criteria.list();
            Collections.reverse(resultList);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return resultList;
    }

    public List<BookExtendedModel> getAllBooksByTransportation(Transportation transportation) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        List<BookExtendedModel> result = new ArrayList<BookExtendedModel>();

        try {
            Query query = this.session.createQuery("select bo.bookNumber as bookNumber, bo.title as title, sum(b.booksCount * b.boxesCount) as count "
                    + "from Book bo "
                    + "join bo.boxes b "
                    + "where bo.transportation = :transportation "
                    + "group by bo.bookNumber "
            );
            query.setParameter("transportation", transportation);
            query.setResultTransformer(Transformers.aliasToBean(BookExtendedModel.class));
            result = (List<BookExtendedModel>) query.list();

        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return result;
    }

    public List<BookModel> getBookModelsByTransportation(Transportation transportation) {
        List<BookModel> resultList = new ArrayList<BookModel>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Book.class);
            criteria.add(Restrictions.eq("transportation", transportation));
            criteria.setProjection(Projections.projectionList()
                    .add(Projections.property("bookNumber"), "bookNumber")
                    .add(Projections.property("title"), "title"))
                    .setResultTransformer(Transformers.aliasToBean(BookModel.class));

            resultList = criteria.list();
            Collections.reverse(resultList);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return resultList;
    }

    public boolean updateBook(Book book) {
        boolean updated = false;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            this.session.saveOrUpdate(book);
            transaction.commit();
            updated = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return updated;
    }

    public String getBiggestBookNumberForTransportation(Transportation transportation) {
        String biggestBookspackageNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery("select max(book.bookNumber) from Book book where book.transportation = :transportation");
            query.setParameter("transportation", transportation);
            biggestBookspackageNumber = (String) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        this.session.close();

        return biggestBookspackageNumber;
    }

    public boolean deleteBook(Book selectedBook) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean isDeleted = false;

        try {
            this.session.delete(selectedBook);
            transaction.commit();
            isDeleted = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }

        return isDeleted;
    }
}
