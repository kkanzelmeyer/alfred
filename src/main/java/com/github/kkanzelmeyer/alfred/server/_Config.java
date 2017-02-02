package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author kevin
 *
 */
public class _Config
{

  private String imageDir = "images/";
  private String environment = "";
  // timeout (seconds) before resetting the doorbell to INACTIVE
  private int doorbellReset = 600; // 10 minutes
  
  // motion detection algorithm settings
  private int motionInterval = 1000;
  private double areaThreshold = 10;
  private int pixelThreshold = 40;
  private int inertia = 1000;
  
  // Google Cloud info
  private String bucket = "";
  private String authFilePath = "";
  
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
  
  

}
