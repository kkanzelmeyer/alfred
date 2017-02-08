package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public interface IAlertService {
  boolean sendAlert(String imagePath, String msg);

  boolean sendAlert(String msg);

  ServiceType getType();
}
