package com.github.kkanzelmeyer.alfred.storage;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public enum StorageBridge {
  INSTANCE;
  
  public String getFileName() {
    Calendar today = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String date = df.format(today.getTime());
    String filename = "visitor" + date + ".jpg";
    return filename;
  }
}
