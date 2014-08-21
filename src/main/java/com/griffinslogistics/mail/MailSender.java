/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.griffinslogistics.mail;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 *
 * @author Georgi
 */
public class MailSender {
    private static final Logger logger = Logger.getLogger(MailSender.class.getName());
    
    public static void sendMail(String messageText) {
        try {
            String from = "noreply.griffinslogistics@gmail.com";
            String password = "martogriffins";
            
            // TODO: fill-in
            String to = null;
            
            Properties props = System.getProperties();
            Session session = Session.getInstance(props, null);

            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Транспортът е променен", "UTF-8");
            message.setText(messageText, "UTF-8");

            Transport tr = session.getTransport("smtp");
            tr.connect("smtp.gmail.com", from, password);
            tr.sendMessage(message, message.getAllRecipients());
            tr.close();

        } catch (AddressException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        } catch (MessagingException ex) {
           logger.log(Level.SEVERE, ex.getMessage());
        }
    }
}
