package com.github.kkanzelmeyer.alfred.config;

import java.io.File;
import java.io.IOException;

import com.github.kkanzelmeyer.alfred.server.Config;

/**
 * Created by kevinkanzelmeyer on 2/8/17.
 */
public class GenerateConfig {

  public static void main(String[] args) {
    // update directory for your machine
    String directory = System.getProperty("user.home") + "/alfred-config/";

    // if the directory doesn't exist create it
    File dir = new File(directory);
    if (!dir.exists()) {
      dir.mkdirs();
    }

    // create model sap files
    Config config = new Config();
    try {
      config.exportAsJsonFile(directory + "alfred-config.json");
    } catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

}
