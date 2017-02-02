package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.IAlfredBridge;

public enum StorageBridge implements IAlfredBridge {
  INSTANCE;
  
  private final Logger logger = LoggerFactory.getLogger(getDeclaringClass());
  private List<IStorageService> storageServices = null;
  
  public String getFileName() {
    Calendar today = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    String date = df.format(today.getTime());
    String filename = "visitor" + date + ".jpg";
    return filename;
  }
  
  public void saveImage(BufferedImage img) {
    for (IStorageService storage : storageServices)
    {
      logger.debug("{} saving image", storage.getClass().getSimpleName());
      storage.saveImage(img);
    }
  }

  @Override
  public void setup() {
    // TODO Auto-generated method stub
    
  }
}
