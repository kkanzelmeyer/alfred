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

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.RPDoorbellPluginWebcam;

public enum Server
{
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  /**
   * initialize the state devices and plugins
   */
  public void init()
  {
    LOG.trace("Initializing server");

    // create device and add it to the manager
    LOG.trace("Creating state device");
    StateDevice doorbell = new StateDevice(new Builder()
        .setId("front-door")
        .setName("Front Door")
        .setState(State.INACTIVE)
        .setType(Type.DOORBELL_WEBCAM)
        .build());

    LOG.trace("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);

    // initialize device plugin
    new RPDoorbellPluginWebcam(12, doorbell).activate();

  }

  public void run()
  {
    LOG.trace("Running server");

  }

  /**
   * Method to send an email message to all email clients
   * 
   * @param email
   *          A reference to a valid email object
   */
  public static void sendEmail(Email email)
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
      }
      catch (MessagingException e)
      {
        LOG.error("Email error", e);
        throw new RuntimeException(e);
      }
    }
  }

  /**
   * Simple method to print a greeting at the startup of the server
   */
  public static void printGreeting()
  {
    LOG.info( "\n-----------------------------------------------------------" + 
              "\n             Alfred Home Server"
            + "\n-----------------------------------------------------------" + "\n");
    LOG.info("Starting Alfred Server");
  }
}
