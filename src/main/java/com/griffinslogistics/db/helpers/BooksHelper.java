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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.griffinslogistics.models.BookBoxModel;
import com.griffinslogistics.models.BookExtendedModel;
import com.griffinslogistics.models.BookLabelModel;
import com.griffinslogistics.models.BookModel;
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

    private static final String QUERY_BOOK_BOX_MODELS_BY_PACKAGE = "select bo.bookNumber as bookNumber, bo.title as title, b.booksCount as booksCount, sum(b.boxesCount) as boxesCount "
            + "from Book bo "
            + "join bo.boxes b "
            + "where bo.bookspackage = :bookspackage "
            + "group by b.booksCount, bo.bookNumber "
            + "order by bo.bookNumber ";

    private static final String QUERY_BOOK_EXTENDED_MODEL_BY_TRANSPORTATION = "select bo.bookNumber as bookNumber, bo.title as title, sum(b.booksCount * b.boxesCount) as count "
            + "from Book bo "
            + "join bo.boxes b "
            + "where bo.transportation = :transportation "
            + "group by bo.bookNumber ";

    private static final String QUERY_BOOKS_TOTALS_BY_BOOKSPACKAGE = "select bo.id as id, bo.bookNumber as bookNumber, bo.weight as weight, bo.weightPerBook as weightPerBook, bo.title as title, bo.count as count, sum(bx.booksCount * bx.boxesCount) as totalBooksCount, sum(bx.booksCount * bx.boxesCount * bo.weightPerBook) as totalBooksWeight "
            + "from Book bo "
            + "join bo.boxes bx "
            + "where bo.bookspackage = :bookspackage "
            + "group by bo.id";

    private static final String QUERY_BOOK_LABEL_MODEL = "select book.bookNumber as bookNumber, book.title as title, book.client as client, sum(box.booksCount * box.boxesCount), transportation.  "
            + "from Book book "
            + "join book.boxes box "
            + "join book.transportation transportation"
            + "where book = :book ";

    private Session session;

    public BooksHelper() {
    }

    public List<Book> getAllBooks() {
        List<Book> resultList = new ArrayList<Book>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Criteria criteria = this.session.createCriteria(Book.class);
            resultList = criteria.list();
            Collections.reverse(resultList);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }
        return resultList;
    }

    public List<Book> getBooksByBookspackage(Bookspackage bookspackage) {

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        List<Book> result = new ArrayList<Book>();

        try {
            bookspackage = (Bookspackage) this.session.get(Bookspackage.class, bookspackage.getId());
            result = bookspackage.getBooks();

            for (Book book : result) {
                long totalBooksCount = 0;
                double totalBooksWeight = 0;
                for (Box box : (Set<Box>) book.getBoxes()) {
                    int boxesCount = box.getBoxesCount();
                    int booksCount = box.getBooksCount();

                    totalBooksCount += boxesCount * booksCount;
                    totalBooksWeight += boxesCount * booksCount * book.getWeightPerBook();
                }

                book.setTotalBooksCount(totalBooksCount);
                book.setTotalBooksWeight(totalBooksWeight);
            }

//            Query query = this.session.createQuery(QUERY_BOOKS_TOTALS_BY_BOOKSPACKAGE);
//            query.setParameter("bookspackage", bookspackage);
//            query.setResultTransformer(Transformers.aliasToBean(Book.class));
//            result = (List<Book>) query.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

        return result;
//
//        List<Book> resultList = new ArrayList<Book>();
//
//        this.session = HibernateUtil.getSessionFactory().openSession();
//        Transaction transaction = this.session.beginTransaction();
//
//        try {
//            Criteria criteria = this.session.createCriteria(Book.class);
//            criteria.add(Restrictions.eq("bookspackage", bookspackage));
//            resultList = criteria.list();
//            Collections.reverse(resultList);
//            transaction.commit();
//        } catch (HibernateException e) {
//            transaction.rollback();
//            logger.log(Level.SEVERE, e.getMessage());
//        }
//
//        this.session.close();
//
//        return resultList;
    }

    public List<BookExtendedModel> getAllBooksByTransportation(Transportation transportation) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        List<BookExtendedModel> result = new ArrayList<BookExtendedModel>();

        try {
            Query query = this.session.createQuery(QUERY_BOOK_EXTENDED_MODEL_BY_TRANSPORTATION);
            query.setParameter("transportation", transportation);
            query.setResultTransformer(Transformers.aliasToBean(BookExtendedModel.class));
            result = (List<BookExtendedModel>) query.list();

        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

        return result;
    }

    public List<BookBoxModel> getAllBookBoxModelsByPackage(Bookspackage bookspackage) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        List<BookBoxModel> result = new ArrayList<BookBoxModel>();

        try {
            Query query = this.session.createQuery(QUERY_BOOK_BOX_MODELS_BY_PACKAGE);

            query.setParameter("bookspackage", bookspackage);
            query.setResultTransformer(Transformers.aliasToBean(BookBoxModel.class));
            result = (List<BookBoxModel>) query.list();

        } catch (HibernateException e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            this.session.close();
        }

        return result;
    }

    public Map<String, List<BookBoxModel>> getAllBookBoxModelsByTransportation(Transportation transportation) {
        Map<String, List<BookBoxModel>> result = new HashMap<String, List<BookBoxModel>>();

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
//            transportation = (Transportation) this.session.merge(transportation);
            transportation = (Transportation) this.session.get(Transportation.class, transportation.getId());

            for (Bookspackage bookspackage : transportation.getBookspackages()) {
                Query query = this.session.createQuery(QUERY_BOOK_BOX_MODELS_BY_PACKAGE);

                query.setParameter("bookspackage", bookspackage);
                query.setResultTransformer(Transformers.aliasToBean(BookBoxModel.class));
                List<BookBoxModel> bookBoxModels = (List<BookBoxModel>) query.list();
                if (bookBoxModels.size() > 0) {
                    result.put(bookspackage.getPackageNumber(), bookBoxModels);
                }
            }

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

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
                    .add(Projections.groupProperty("bookNumber"), "bookNumber")
                    .add(Projections.property("title"), "title"))
                    .setResultTransformer(Transformers.aliasToBean(BookModel.class));

            resultList = criteria.list();
            Collections.reverse(resultList);
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

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
        } finally {
            this.session.close();
        }

        return updated;
    }

    public Integer getBiggestBookNumberForTransportation(Transportation transportation) {
        Integer biggestBookspackageNumber = null;

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        try {
            Query query = this.session.createQuery("select max(book.bookNumber) from Book book where book.transportation = :transportation");
            query.setParameter("transportation", transportation);
            biggestBookspackageNumber = (Integer) query.uniqueResult();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

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
        } finally {
            this.session.close();
        }

        return isDeleted;
    }

    public BookLabelModel getLabelInfoForBook(Book book) {
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        BookLabelModel result = null;

        try {
//            book = (Book) this.session.merge(book);
            book = (Book) this.session.get(Book.class, book.getId());

            Long count = 0L;

            for (Box box : (Set<Box>) book.getBoxes()) {
                count += box.getBooksCount() * box.getBoxesCount();
            }
            Transportation transportation = book.getTransportation();
            Bookspackage bookspackage = book.getBookspackage();
            result = new BookLabelModel(bookspackage.getDeliveryAddress(), bookspackage.getPostalCode(),
                    book.getTitle(), book.getBookNumber(), bookspackage.getClient(),
                    transportation.getWeekNumber() + "/" + transportation.getYear(), count);

            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();
        }

        return result;
    }

}
