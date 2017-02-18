package com.github.kkanzelmeyer.alfred.alert;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.IAlfredService;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public enum AlertBridge implements IAlfredService {
  INSTANCE;

  private Logger logger = LoggerFactory.getLogger(AlertBridge.class);

  private List<IAlertService> alertServices = new ArrayList<>();

  @Override
  public void setup() {
    if (alertServices.size() == 0) {
      logger.warn("No storage services added - did you mean to add storage services?");
    }
    for (IAlertService service : alertServices) {
      service.setup();
    }
  }

  /**
   * @param imagePaths
   * @param msg
   * @return
   */
  public boolean sendAlert(Map<ServiceType, String> imagePaths, String msg) {
    for (IAlertService service : alertServices) {
      logger.debug("Sending alert to {}", service.getClass());
      service.sendAlert(imagePaths.get(service.getType()), msg);
    }
    return false;
  }

  /**
   * @param msg
   * @return
   */
  public boolean sendAlert(String msg) {
    for (IAlertService service : alertServices) {
      logger.debug("Sending alert to {}", service.getClass());
      service.sendAlert(msg);
    }
    return false;
  }

  /**
   *
   * @param fqn
   *          the fully qualified class path to the service
   */
  public void addService(String fqn) {
    try {
      logger.info("adding alert service: {}", fqn);
      Class<?> clazz = Class.forName(fqn);
      Constructor<?> constructor = clazz.getConstructor();
      Object instance = constructor.newInstance();
      if (!IAlertService.class.isAssignableFrom(instance.getClass())) {
        throw new ClassCastException("Unrecognized storage service: " + instance.getClass().getName());
      }
      IAlertService service = (IAlertService) instance;
      alertServices.add(service);
    } catch (Exception e) {
      logger.error("Error adding service: " + fqn, e);
    }
  }

}
