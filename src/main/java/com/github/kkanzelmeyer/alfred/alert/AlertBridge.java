package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.IAlfredService;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public enum AlertBridge implements IAlfredService {
  INSTANCE;

  private Logger logger = LoggerFactory.getLogger(AlertBridge.class);

  private List<IAlertService> alertServices = null;

  @Override
  public void setup() {
    if (alertServices == null) {
      alertServices = new ArrayList<>();
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

}
