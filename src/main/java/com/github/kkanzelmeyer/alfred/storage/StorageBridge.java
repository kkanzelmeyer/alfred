package com.github.kkanzelmeyer.alfred.storage;

import com.github.kkanzelmeyer.alfred.IAlfredBridge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.image.BufferedImage;
import java.lang.reflect.Constructor;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public enum StorageBridge implements IAlfredBridge {
  INSTANCE;

  private final Logger logger = LoggerFactory.getLogger(getDeclaringClass());
  private List<IStorageService> storageServices = new ArrayList<>();
  
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
    if (storageServices.size() == 0) {
      logger.warn("No storage services added - did you mean to add storage services?");
    }
    for (IStorageService service : storageServices) {
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
      storageServices.add(service);
    } catch (Exception e) {
      logger.error("Error adding service: " + fqn, e);
    }
  }
}
