package com.github.kkanzelmeyer.alfred.server;

import java.awt.Rectangle;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

/**
 * @author kevin
 */
public class Config {

  public String imageDir = "";
  public String environment = "development";
  public ArrayList<String> emailRecipients = new ArrayList<>();
  // timeout (seconds) before resetting the doorbell to INACTIVE
  public int doorbellReset = 300; // 10 minutes

  // motion detection algorithm settings
  public int motionInterval = 1000;
  public double areaThreshold = 10;
  public double maxAreaThreshold = 100;
  public int pixelThreshold = 40;
  public int inertia = 1000;
  public Rectangle dne = new Rectangle(0, 0);

  // Email saps
  public String auth = "";
  public String ttls = "";
  public String host = "";
  public String port = "";
  public String username = "";
  public String token = "";

  // Google Cloud Storage info
  public String bucket = "";
  public String projectId = "";
  public String authFilePath = "";

  // FCM info
  public String fcmServerKey = "";
  public ArrayList<String> deviceTokens = new ArrayList<>();

  // services
  public ArrayList<String> storageServices = new ArrayList<>(
      Arrays.asList("com.github.kkanzelmeyer.alfred.storage.LocalStorage"));

  public ArrayList<String> alertServices = new ArrayList<>();;

  public Properties getEmailProperties() {
    Properties props = new Properties();
    props.put("mail.smtp.auth", auth);
    props.put("mail.smtp.starttls.enable", ttls);
    props.put("mail.smtp.host", host);
    props.put("mail.smtp.port", port);
    props.put("mail.username", username);
    props.put("mail.token", token);
    props.put("mail.smtp.connectiontimeout", "3000");
    return props;
  }

  public String toJson() {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(this);
    return json;
  }

  /**
   * This method saves the instance of a config object to a file in JSON. This
   * can be helpful if you set different parameters during testing and want to
   * quickly export the settings to a file.
   *
   * @param path The path with filename where you'd like to save the config
   * @throws IOException
   */
  public void exportAsJsonFile(String path) throws IOException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    String json = gson.toJson(this);

    FileWriter writer = new FileWriter(path);
    writer.write(json);
    writer.close();
  }

  /**
   * This static method creates a Sap object of the desired type from a file at
   * the specified path
   *
   * @param path The path to the sap file
   * @return saps The saps object
   * @throws FileNotFoundException
   */
  public static Config createConfig(String path) throws FileNotFoundException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    BufferedReader br = new BufferedReader(new FileReader(path));

    Config saps = gson.fromJson(br, Config.class);
    return saps;
  }

  /**
   * This static method creates a Sap object of the desired type from an input
   * Json object
   *
   * @param json The json representation of the sap object
   * @return saps The saps object
   */
  public static Config createSaps(JsonElement json) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    Config saps = gson.fromJson(json, Config.class);
    return saps;
  }
}
