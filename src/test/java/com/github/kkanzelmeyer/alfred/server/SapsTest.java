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
    ArrayList<String> numbers = Config.INSTANCE.getNumbers();
    for(String number : numbers) {
      LOG.debug("Number: {}", number);
    }
    LOG.debug("Doorbell Reset: {}", Config.INSTANCE.getDoorbellReset());
    assertEquals("Numbers should be equal", 120, Config.INSTANCE.getDoorbellReset());
  }
}
