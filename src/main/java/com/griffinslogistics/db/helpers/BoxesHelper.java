/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.Book;
import com.griffinslogistics.db.entities.Box;
import com.griffinslogistics.db.entities.HibernateUtil;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;

/**
 *
 * @author Georgi
 */
public class BoxesHelper implements Serializable {

    private static final Logger logger = Logger.getLogger(BoxesHelper.class.getName());
    private static final String CLASS_NAME = BoxesHelper.class.getSimpleName();

    private Session session;

    public BoxesHelper() {
    }

    public List<Box> getBoxesByBook(Book book) {
        logger.log(Level.SEVERE, "{0}: getBoxesByBook started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        List<Box> result = new ArrayList<Box>();

        try {
            Criteria criteria = this.session.createCriteria(Box.class);
            criteria.add(Restrictions.eq("book", book));
            result = criteria.list();
            transaction.commit();
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: getBoxesByBook finished", CLASS_NAME);
        }

        return result;
    }

    public boolean updateBox(Box box) {
        logger.log(Level.SEVERE, "{0}: updateBox started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean updated = false;

        try {
            this.session.saveOrUpdate(box);
            transaction.commit();
            updated = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: updateBox finished", CLASS_NAME);
        }

        return updated;
    }

    public boolean deleteBox(Box box) {
        logger.log(Level.SEVERE, "{0}: deleteBox started", CLASS_NAME);

        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();

        boolean updated = false;

        try {
            this.session.delete(box);
            transaction.commit();
            updated = true;
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } finally {
            this.session.close();

            logger.log(Level.SEVERE, "{0}: deleteBox finished", CLASS_NAME);
        }

        return updated;
    }
}
