package com.github.kkanzelmeyer.alfred.service;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.server.VisitorEmail;

public enum AlertService {
  INSTANCE;
  
  private Logger logger = LoggerFactory.getLogger(AlertService.class);
  
  public boolean sendAlert(String imagePath, String msg)
  {
    logger.debug("Sending alert");
    try
    {
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
      Server.INSTANCE.sendEmail(email);
      return true;
    }
    catch(Exception e)
    {
      logger.error("Unable to send image as alert", e);
    }
    return false;
  }

}
