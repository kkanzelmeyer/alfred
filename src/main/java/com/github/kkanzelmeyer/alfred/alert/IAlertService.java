package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.IAlfredService;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public interface IAlertService extends IAlfredService {
  boolean sendAlert(String imagePath, String msg);

  boolean sendAlert(String msg);

  ServiceType getType();
}
