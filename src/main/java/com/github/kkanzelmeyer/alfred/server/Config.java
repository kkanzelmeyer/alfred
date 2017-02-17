package com.github.kkanzelmeyer.alfred.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

/**
 * @author kevin
 */
public class Config {

  public final String imageDir = "";
  public final String environment = "development";
  public final ArrayList<String> emailRecipients = new ArrayList<>();
  // timeout (seconds) before resetting the doorbell to INACTIVE
  public final int doorbellReset = 600; // 10 minutes

  // motion detection algorithm settings
  public final int motionInterval = 1000;
  public final double areaThreshold = 10;
  public final double maxAreaThreshold = 100;
  public final int pixelThreshold = 40;
  public final int inertia = 1000;
  public final Rectangle dne = new Rectangle(0, 0);

  // Email saps
  public final String auth = "";
  public final String ttls = "";
  public final String host = "";
  public final String port = "";
  public final String username = "";
  public final String token = "";

  // Google Cloud Storage info
  public final String bucket = "";
  public final String projectId = "";
  public final String authFilePath = "";

  // FCM info
  public final String fcmServerKey = "";
  public final List<String> deviceTokens = null;

  // services
  public final ArrayList<String> storageServices = new ArrayList<>(
      Arrays.asList("com.github.kkanzelmeyer.alfred.storage.LocalStorage"));

  public final ArrayList<String> alertServices = null;

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
