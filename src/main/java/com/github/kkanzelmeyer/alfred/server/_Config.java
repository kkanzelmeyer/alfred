package com.github.kkanzelmeyer.alfred.server;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author kevin
 */
public class _Config {

  private String imageDir = "Alfred/images/";
  private String environment = "";
  private List<String> emailRecipients = new ArrayList<>();
  // timeout (seconds) before resetting the doorbell to INACTIVE
  private int doorbellReset = 600; // 10 minutes

  // motion detection algorithm settings
  private int motionInterval = 1000;
  private double areaThreshold = 10;
  private double maxAreaThreshold = 100;
  private int pixelThreshold = 40;
  private int inertia = 1000;
  private Rectangle dne = new Rectangle(0, 0);

  // Email saps
  private String auth = "";
  private String ttls = "";
  private String host = "";
  private String port = "";
  private String username = "";
  private String token = "";

  // Google Cloud Storage info
  private String bucket = "";
  private String authFilePath = "";

  // FCM info
  private String fcmServerKey = "";
  public final List<String> deviceTokens = null;

  // services
  private ArrayList<String> storageServices = new ArrayList<>(
                                                                 Arrays.asList("com.github.kkanzelmeyer.alfred.storage.LocalStorage"));

  public String getImageDir() {
    return imageDir;
  }

  public void setImageDir(String imageDir) {
    this.imageDir = imageDir;
  }

  public String getEnvironment() {
    return environment;
  }

  public void setEnvironment(String environment) {
    this.environment = environment;
  }

  public int getDoorbellReset() {
    return doorbellReset;
  }

  public void setDoorbellReset(int doorbellReset) {
    this.doorbellReset = doorbellReset;
  }

  public int getMotionInterval() {
    return motionInterval;
  }

  public void setMotionInterval(int motionInterval) {
    this.motionInterval = motionInterval;
  }

  public double getAreaThreshold() {
    return areaThreshold;
  }

  public void setAreaThreshold(double areaThreshold) {
    this.areaThreshold = areaThreshold;
  }

  public int getPixelThreshold() {
    return pixelThreshold;
  }

  public void setPixelThreshold(int pixelThreshold) {
    this.pixelThreshold = pixelThreshold;
  }

  public int getInertia() {
    return inertia;
  }

  public void setInertia(int inertia) {
    this.inertia = inertia;
  }

  public String getBucket() {
    return bucket;
  }

  public void setBucket(String bucket) {
    this.bucket = bucket;
  }

  public String getAuthFilePath() {
    return authFilePath;
  }

  public void setAuthFilePath(String authFilePath) {
    this.authFilePath = authFilePath;
  }

  public ArrayList<String> getStorageServices() {
    return storageServices;
  }

  public void setStorageServices(ArrayList<String> storageServices) {
    this.storageServices = storageServices;
  }

  public List<String> getEmailRecipients() {
    return emailRecipients;
  }

  public String getFcmServerKey() {
    return fcmServerKey;
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
  public static _Config createConfig(String path) throws FileNotFoundException {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    BufferedReader br = new BufferedReader(new FileReader(path));

    _Config saps = gson.fromJson(br, _Config.class);
    return saps;
  }

  /**
   * This static method creates a Sap object of the desired type from an input
   * Json object
   *
   * @param json The json representation of the sap object
   * @return saps The saps object
   */
  public static _Config createSaps(JsonElement json) {
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    _Config saps = gson.fromJson(json, _Config.class);
    return saps;
  }

  public Rectangle getDne() {
    return dne;
  }

  public double getMaxAreaThreshold() {
    return maxAreaThreshold;
  }
}
