package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;

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

public enum Server
{
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  /**
   * Method to send an email message to all email clients
   * 
   * @param email
   *          A reference to a valid email object
   */
  public boolean sendEmail(Email email)
  {
    // only send if the environment is production
    if (Config.INSTANCE.getEnvironment().equals("production"))
    {
      ArrayList<String> emailClients = Config.INSTANCE.getEmails();
      if (emailClients.size() > 0)
      {
        final String username = Config.INSTANCE.getUsername();
        final String password = Config.INSTANCE.getToken();

        Session session = Session.getInstance(Config.INSTANCE.getEmailProperties(), new javax.mail.Authenticator()
        {
          protected PasswordAuthentication getPasswordAuthentication()
          {
            return new PasswordAuthentication(username, password);
          }
        });

        // convert clients list into a comma separated string
        String clients = StringUtils.implode(emailClients, ",");
        try
        {
          LOG.debug("Email on thread {}", Thread.currentThread().getId());
          Message message = new MimeMessage(session);
          message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(clients));
          message.setSubject(email.getSubject());

          // add email content to message
          message.setContent(email.getContent());

          // send email
          Transport.send(message);
          LOG.info("Email sent to {}", clients);
          return true;
        }
        catch (MessagingException e)
        {
          LOG.error("Email error", e);
          throw new RuntimeException(e);
        }
      }
    }
    else
    {
      LOG.debug("Warning: In development environment an email is not actually sent");
    }
    return false;
  }

  /**
   * Simple method to print a greeting at the startup of the server
   */
  public void printGreeting()
  {
    LOG.info("\n-----------------------------------------------------------" +
        "\n             Alfred Home Server"
        + "\n-----------------------------------------------------------" + "\n");
    LOG.info("Starting Alfred Server");
  }
}
