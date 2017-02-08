package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public class FirebaseAlert implements IAlertService {

  private final ServiceType type = ServiceType.FIREBASE;
  
  @Override
  public boolean sendAlert(String imagePath, String msg) {

    return false;
  }

  @Override
  public boolean sendAlert(String msg) {
    return false;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

}