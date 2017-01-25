package com.github.kkanzelmeyer.alfred.server;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMultipart;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Visitor Email
 * 
 * This is the template for the email that is sent when a visitor is detected by
 * a doorbell device
 * 
 * @author kevin
 *
 */
public class VisitorEmail extends Email
{

  private final Logger LOG = LoggerFactory.getLogger(VisitorEmail.class);

  @Override
  public MimeMultipart getContent()
  {
    MimeMultipart multipart = new MimeMultipart("related");
    // first part (the html)
    BodyPart messageBodyPart = new MimeBodyPart();
    String htmlText = "<h3>" + getDisplayDate() + "</h3>"
        + "<p>" + getMsg() + "<p>"
        + "<img src=\"cid:image\">";
    try
    {
      messageBodyPart.setContent(htmlText, "text/html");
      // add it
      multipart.addBodyPart(messageBodyPart);

      // second part (the image)
      messageBodyPart = new MimeBodyPart();
      DataSource fds = new FileDataSource(getImagePath());

      messageBodyPart.setDataHandler(new DataHandler(fds));
      messageBodyPart.setHeader("Content-ID", "<image>");

      // add image to the multipart
      multipart.addBodyPart(messageBodyPart);
    }
    catch (MessagingException e)
    {
      LOG.error("Unable to create message", e);
    }
    return multipart;
  }

}
