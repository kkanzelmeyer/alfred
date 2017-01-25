package com.github.kkanzelmeyer.alfred.datastore;

import com.github.kkanzelmeyer.alfred.server._Config;

public enum AlfredStore {
  INSTANCE;
  
  private _Config config;
  
  public void setConfig(_Config config) {
    this.config = config;
  }
  public _Config getConfig() {
    return config;
  }
}
