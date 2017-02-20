package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.IAlfredBridge;

public enum StorageBridge implements IAlfredBridge {
  INSTANCE;

  private final Logger logger = LoggerFactory.getLogger(getDeclaringClass());
  private Map<ServiceType, IStorageService> storageServices = new HashMap<>();
  
  public String getDate() {
    Calendar today = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    return df.format(today.getTime());
  }

  public String getFileName() {
    Calendar today = Calendar.getInstance();
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd_hh-mm-ss");
    String date = df.format(today.getTime());
    String filename = "visitor-" + date + ".jpg";
    return filename;
  }

  public String saveImage (ServiceType type, BufferedImage img)
  {
    IStorageService storage = storageServices.get(type);
    if (storage == null) {
      logger.warn("saveImage - storage type not activated {}", type);
      return null;
    }
    return storage.saveImage(img);
  }

  @Override
  public void setup() {
    if (storageServices.size() == 0) {
      logger.warn("No storage services added - did you mean to add storage services?");
    }
    for (IStorageService service : storageServices.values()) {
      service.setup();
    }
  }

  /**
   *
   * @param fqn the fully qualified class path to the service
   */
  public void addService(String fqn) {
    try {
      logger.info("adding storage service: {}", fqn);
      Class<?> clazz = Class.forName(fqn);
      Constructor<?> constructor = clazz.getConstructor();
      Object instance = constructor.newInstance();
      if (!IStorageService.class.isAssignableFrom(instance.getClass())) {
        throw new ClassCastException("Unrecognized storage service: " + instance.getClass().getName());
      }
      IStorageService service = (IStorageService) instance;
      storageServices.put(service.getType(), service);
    } catch (Exception e) {
      logger.error("Error adding service: " + fqn, e);
    }
  }
}
