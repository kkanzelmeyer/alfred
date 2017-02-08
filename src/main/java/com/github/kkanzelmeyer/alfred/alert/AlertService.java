package com.github.kkanzelmeyer.alfred.alert;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum AlertService implements IAlfredService {
  INSTANCE;

  private Logger logger = LoggerFactory.getLogger(AlertService.class);

  private List<IAlertService> alertServices = null;

  @Override
  public void setup() {
    if (alertServices == null) {
      alertServices = new ArrayList<>();
    }
  }

  /**
   * @param imagePath
   * @param msg
   * @return
   */
  public boolean sendAlert(String imagePath, String msg) {
    logger.debug("Sending alert");
    for (IAlertService service : alertServices) {
      service.sendAlert(imagePath, msg);
    }
    return false;
  }

}
