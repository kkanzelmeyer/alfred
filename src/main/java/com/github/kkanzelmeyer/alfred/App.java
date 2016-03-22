package com.github.kkanzelmeyer.alfred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.server.VisitorEmail;

/**
 * Hello world!
 *
 */
public class App
{

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args)
  {
    try
    {
      String date = String.valueOf(System.currentTimeMillis());
      VisitorEmail email = new VisitorEmail();
      email.setDate(date);
      email.setImagePath("/home/kevin/Alfred/img/visitor1458397515036.jpg");
      email.setSubject("Visitor at the Front Door");
      Server.sendEmail(email);
    }
    catch (Exception e)
    {
      LOG.error("Exception thrown", e);
    }
  }
}
