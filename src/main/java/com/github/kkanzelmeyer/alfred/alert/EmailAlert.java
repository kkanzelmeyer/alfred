package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.server.Email;
import com.github.kkanzelmeyer.alfred.server.VisitorEmail;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import org.bridj.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

//import javax.mail.Store;

public class EmailAlert implements IAlertService {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private final ServiceType type = ServiceType.LOCAL;
  Session session = null;

  @Override
  public void setup() {
    if (session == null) {
      String username = Store.INSTANCE.getConfig().username;
      String password = Store.INSTANCE.getConfig().token;

      session = Session.getInstance(
          Store.INSTANCE.getConfig().getEmailProperties(), new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
              return new PasswordAuthentication(username, password);
            }
          });
    }

  }

  public boolean sendAlert(String imagePath, String msg) {
    if (Store.INSTANCE.getConfig().environment.equals("development")) {
      logger.info("Environment set to development - not sending email");
      return false;
    }
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

  @Override
  public boolean sendAlert(String msg) {
    return false;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

  /**
   * Email sending function
   *
   * @param email
   * @return
   */
  public boolean sendEmail(Email email) {
    // only send if the environment is production
    ArrayList<String> emailClients = Store.INSTANCE.getConfig().emailRecipients;
    if (emailClients.size() > 0) {
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
    return false;
  }

}
