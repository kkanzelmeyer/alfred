package com.github.kkanzelmeyer.alfred.datastore;

import com.github.kkanzelmeyer.alfred.server.Config;

public enum Store {
  INSTANCE;

  private Config config;

  public void setConfig(Config config) {
    this.config = config;
  }

  public Config getConfig() {
    return config;
  }
}
