package com.github.kkanzelmeyer.alfred.alert;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.IAlfredService;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public enum AlertBridge implements IAlfredService {
  INSTANCE;

  private Logger logger = LoggerFactory.getLogger(AlertBridge.class);

  private Map<ServiceType, IAlertService> alertServices = new HashMap<>();

  @Override
  public void setup() {
    if (alertServices.size() == 0) {
      logger.warn("No alert services added - did you mean to add alert services?");
    }
    for (IAlertService service : alertServices.values()) {
      service.setup();
    }
  }

  /**
   * 
   * @param alert
   */
  public void sendAlert(AlfredAlert alert) {
    IAlertService service = alertServices.get(alert.getType());
    if (service == null) {
      logger.warn("sendAlert - storage type not activated {}", alert.getType());
      return;
    }
    logger.debug("Sending alert to {}", service.getClass());
    service.sendAlert(alert.getImagePath(), alert.getMessage());
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
      alertServices.put(service.getType(), service);
    } catch (Exception e) {
      logger.error("Error adding service: " + fqn, e);
    }
  }


}
