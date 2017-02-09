package com.github.kkanzelmeyer.alfred.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Server
{
  INSTANCE;

  private final _Config config = new _Config();

  public _Config getConfig() {
    return config;
  }

  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

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
