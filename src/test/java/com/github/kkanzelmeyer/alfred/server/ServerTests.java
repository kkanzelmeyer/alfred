package com.github.kkanzelmeyer.alfred.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.utils.ParseJsonFile;

public class ServerTests
{

  final private static Logger LOG = LoggerFactory.getLogger(ServerTests.class);

  @Test
  public void sapsTest()
  {
    LOG.debug("Printing SAPS");
    LOG.debug("Environment: {}", Config.INSTANCE.getEnvironment());
    LOG.debug("ImageDirectory: {}", Config.INSTANCE.getImageDir());
    ArrayList<String> emails = Config.INSTANCE.getEmails();
    for (String email : emails)
    {
      LOG.debug("Email: {}", email);
    }
    LOG.debug("Doorbell Reset: {}", Config.INSTANCE.getDoorbellReset());
    assertEquals("Numbers should be equal", 120, Config.INSTANCE.getDoorbellReset());
  }

  @Test
  public void emailTest()
  {
    ClassLoader classLoader = ParseJsonFile.class.getClassLoader();
    String img = classLoader.getResource("alfred.png").getFile();
    LOG.debug("Setting image file: {}", img);
    
    String date = String.valueOf(System.currentTimeMillis());
    VisitorEmail email = new VisitorEmail();
    email.setDate(date);
    email.setImagePath(img);
    email.setSubject("Visitor at the Front Door");
    assertEquals(true, Server.INSTANCE.sendEmail(email));
  }
}
