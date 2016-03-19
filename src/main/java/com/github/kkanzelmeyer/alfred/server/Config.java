package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;

public enum Config
{

  INSTANCE;

  private String mImageDir = "/home/kevin/Alfred/img";
  private ArrayList<String> mNumbers;
  private final String mEnvironment = System.getenv("JAVA_ENV");
  private int mDoorbellReset = 5;

  static
  {
    // load config file
  }

  public String getImageDir()
  {
    return mImageDir;
  }

  public ArrayList<String> getNumbers()
  {
    return mNumbers;
  }
  
  public String getEnvironment() {
    return mEnvironment;
  }
  
  public int getDoorbellReset() {
    return mDoorbellReset;
  }
  
  public void setDoorbellReset(int val) {
    mDoorbellReset = val;
  }
}
