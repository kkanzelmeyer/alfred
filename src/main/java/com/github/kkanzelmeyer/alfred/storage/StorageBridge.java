package com.github.kkanzelmeyer.alfred.storage;

import com.github.kkanzelmeyer.alfred.IAlfredBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
  
  public Map<ServiceType, String> saveImage(BufferedImage img) {
    Map<ServiceType, String> paths = new HashMap<>();
    for (IStorageService storage : storageServices)
    {
      logger.debug("{} saving image", storage.getClass().getSimpleName());
      paths.put(storage.getType(), storage.saveImage(img));
    }
    return paths;
  }

  @Override
  public void setup() {
    if (storageServices == null) {
      storageServices = new ArrayList<>();
    }
  }

  /**
   *
   * @param service the storage service to add
   */
  public void addService(IStorageService service) {
    if (storageServices == null) {
      throw new NullPointerException("StorageBridge has not been initialized");
    }
    storageServices.add(service);
  }
}
