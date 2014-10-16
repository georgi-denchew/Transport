/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.db.helpers;

import com.griffinslogistics.db.entities.HibernateUtil;
import com.griffinslogistics.db.entities.Pulsiodetails;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.util.IOUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

/**
 *
 * @author Georgi
 */
public class PulsioDetailsHelper implements Serializable{
    private static final Logger logger = Logger.getLogger(PulsioDetailsHelper.class.getName());
    
    private Session session;
    
    public PulsioDetailsHelper(){
    }
    
    public boolean validatePassword(String password){
        boolean result = false;
        
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();
        
        try {
            Query query = this.session.createQuery("select password from Pulsiodetails where password like :password");
            query.setParameter("password", password);
            String passwordResult = (String) query.uniqueResult();
            if (passwordResult != null && passwordResult.equals(password)){
                result = true;
            }
            
        } catch (HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        }finally{
        this.session.close();
        }
        
        return result;
    }
    public Pulsiodetails getDetails(){
        this.session = HibernateUtil.getSessionFactory().openSession();
        Transaction transaction = this.session.beginTransaction();
        
        Pulsiodetails pulsiodetails = null;
        
        try {
            Criteria criteria = this.session.createCriteria(Pulsiodetails.class);
            List list = criteria.list();
            pulsiodetails = (Pulsiodetails) list.get(0);
//            InputStream inputStream = new FileInputStream("D:\\pulsio.jpg");
//            byte[] bytes = IOUtils.toByteArray(inputStream);
//            pulsiodetails.setLogo(bytes);
//            inputStream.close();
            transaction.commit(); 
        } catch(HibernateException e) {
            transaction.rollback();
            logger.log(Level.SEVERE, e.getMessage());
        } 
//        catch (FileNotFoundException ex) {
//            Logger.getLogger(PulsioDetailsHelper.class.getName()).log(Level.SEVERE, null, ex);
//        } catch (IOException ex) {
//            Logger.getLogger(PulsioDetailsHelper.class.getName()).log(Level.SEVERE, null, ex);
//        }
        finally{
        this.session.close();
        }
        
        return pulsiodetails;
    }
    
}
