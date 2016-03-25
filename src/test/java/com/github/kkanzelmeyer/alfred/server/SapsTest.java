package com.github.kkanzelmeyer.alfred.server;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SapsTest
{
  

  final private static Logger LOG = LoggerFactory.getLogger(SapsTest.class);
  
  @Test
  public void sapsTest() {
    
    LOG.debug("Printing SAPS");
    LOG.debug("Environment: {}", Config.INSTANCE.getEnvironment());
    LOG.debug("ImageDirectory: {}", Config.INSTANCE.getImageDir());
    ArrayList<String> emails = Config.INSTANCE.getEmails();
    for(String email : emails) {
      LOG.debug("Email: {}", email);
    }
    int doorbellReset = Config.INSTANCE.getDoorbellReset();
    LOG.debug("Doorbell Reset: {}", Config.INSTANCE.getDoorbellReset());
    assertEquals("Numbers should be equal", doorbellReset, Config.INSTANCE.getDoorbellReset());
  }
}
