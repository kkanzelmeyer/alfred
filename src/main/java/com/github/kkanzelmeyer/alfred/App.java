package com.github.kkanzelmeyer.alfred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Server;

/**
 * Hello world!
 *
 */
public class App
{
  final private static Logger LOG = LoggerFactory.getLogger(App.class);
  
  public static void main(String[] args)
  {
    LOG.trace("Initializing Server");
    Server.INSTANCE.init();
  }
}
