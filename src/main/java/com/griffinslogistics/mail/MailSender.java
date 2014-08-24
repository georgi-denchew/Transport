/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.griffinslogistics.mail;

import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 *
 * @author Georgi
 */
public class MailSender {

    private static final Logger logger = Logger.getLogger(MailSender.class.getName());

    public static void sendMail(String messageText) {
        try {
//            String from = "noreply.griffinslogistics@gmail.com";
//            String password = "martogriffins";

//            String from = "no-reply@griffins-logistics.com";
//            String password = "m4r70";
//
//            // TODO: fill-in
//            String to = "georgi.denchew@gmail.com";
//
//            Properties props = System.getProperties();
//            Session session = Session.getInstance(props, null);
//
//            MimeMessage message = new MimeMessage(session);
//            message.setFrom(new InternetAddress(from));
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(to));
//            message.setSubject("Транспортът е променен", "UTF-8");
//            message.setText(messageText, "UTF-8");
//
//            Transport tr = session.getTransport("smtp");
////            tr.connect("smtp.gmail.com", from, password);
//            tr.connect("mail.sbnd.net", from, password);
//
//            tr.sendMessage(message, message.getAllRecipients());
//            tr.close();
            Properties props = System.getProperties();
            props.put("mail.smtp.starttls.enable", true); // added this line
            props.put("mail.smtp.host", "mail.sbnd.net");
            props.put("mail.smtp.user", "no-reply@griffins-logistics.com");
            props.put("mail.smtp.password", "m4r70");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", true);

            Session session = Session.getInstance(props, null);
            
            InternetAddress from = new InternetAddress("no-reply@griffins-logistics.com");
            
            MimeMessage message = new MimeMessage(session);
            message.setFrom(from);
            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("georgi.denchew@gmail.com"));
            message.setSubject("Транспортът е променен", "UTF-8");
            message.setText(messageText, "UTF-8");
            
            
//            MimeMessage message = new MimeMessage(session);
//
//            System.out.println("Port: " + session.getProperty("mail.smtp.port"));
//
//            // Create the email addresses involved
//            message.setSubject("Транспортът е променен", "UTF-8");
//            message.setFrom(from);
//            message.addRecipients(Message.RecipientType.TO, InternetAddress.parse("georgi.denchew@gmail.com"));
//
//            // Create a multi-part to combine the parts
//            Multipart multipart = new MimeMultipart("alternative");
//
//            // Create your text message part
//            BodyPart messageBodyPart = new MimeBodyPart();
////            messageBodyPart.setText("some text to send");
//            messageBodyPart.setText(messageText);
//            
//            // Add the text part to the multipart
//            multipart.addBodyPart(messageBodyPart);
//
//            // Create the html part
//            messageBodyPart = new MimeBodyPart();
//            String htmlMessage = messageText;
//            messageBodyPart.setContent(htmlMessage, "text/html");
//
//            // Add html part to multi part
//            multipart.addBodyPart(messageBodyPart);
//
//            // Associate multi-part with message
//            message.setContent(multipart);

            // Send message
            Transport transport = session.getTransport("smtp");
            transport.connect("mail.sbnd.net", "no-reply@griffins-logistics.com", "m4r70");
            System.out.println("Transport: " + transport.toString());
            transport.sendMessage(message, message.getAllRecipients());

        } catch (AddressException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        } catch (MessagingException ex) {
            logger.log(Level.SEVERE, ex.getMessage());
        }
    }

}
