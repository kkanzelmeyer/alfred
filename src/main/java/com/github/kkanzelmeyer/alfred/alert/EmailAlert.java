package com.github.kkanzelmeyer.alfred.alert;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.bridj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Config;
import com.github.kkanzelmeyer.alfred.server.Email;
import com.github.kkanzelmeyer.alfred.server.VisitorEmail;

public class EmailAlert implements IAlertService {

  private final Logger logger = LoggerFactory.getLogger(getClass());

  public boolean sendAlert(String imagePath, String msg) {
    logger.debug("Sending alert");
    try {
      // send alerts
      String date = String.valueOf(System.currentTimeMillis());
      Calendar today = Calendar.getInstance();
      DateFormat df = new SimpleDateFormat("MMM dd h:mm a");
      String prettyDate = df.format(today.getTime());
      VisitorEmail email = new VisitorEmail();
      email.setDate(date);
      email.setMsg(msg);
      email.setImagePath(imagePath);
      email.setSubject("Visitor at the Front Door " + prettyDate);
      sendEmail(email);
      return true;
    } catch (Exception e) {
      logger.error("Unable to send image as alert", e);
    }
    return false;
  }

  /**
   * Email sending function
   * 
   * @param email
   * @return
   */
  public boolean sendEmail(Email email) {
    // only send if the environment is production
    if (Config.INSTANCE.getEnvironment().equals("production")) {
      ArrayList<String> emailClients = Config.INSTANCE.getEmails();
      if (emailClients.size() > 0) {
        final String username = Config.INSTANCE.getUsername();
        final String password = Config.INSTANCE.getToken();

        Session session = Session.getInstance(Config.INSTANCE.getEmailProperties(), new javax.mail.Authenticator() {
          protected PasswordAuthentication getPasswordAuthentication() {
            return new PasswordAuthentication(username, password);
          }
        });

        // convert clients list into a comma separated string
        String clients = StringUtils.implode(emailClients, ",");
        try {
          logger.debug("Email on thread {}", Thread.currentThread().getId());
          Message message = new MimeMessage(session);
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(clients));
          message.setSubject(email.getSubject());

          // add email content to message
          message.setContent(email.getContent());

          // send email
          Transport.send(message);
          logger.info("Email sent to {}", clients);
          return true;
        } catch (MessagingException e) {
          logger.error("Email error", e);
          throw new RuntimeException(e);
        }
      }
    } else {
      logger.debug("Warning: In development environment an email is not actually sent");
    }
    return false;
  }
}
