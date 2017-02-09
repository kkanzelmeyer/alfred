package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;

public class FirebaseAlert implements IAlertService {

  private final ServiceType type = ServiceType.FIREBASE;
  
  @Override
  public boolean sendAlert(String imagePath, String msg) {
    try {
      HttpResponse<JsonNode> jsonResponse = Unirest.post("//fcm.googleapis.com/fcm/send")
                                                .header("accept", "application/json")
                                                .header("Content-Type", "application/json")
                                                .header("Authorization", "key=" + Server.INSTANCE.getConfig().getFcmServerKey())
                                                .queryString("apiKey", "123")
                                                .field("parameter", "value")
                                                .field("foo", "bar")
                                                .asJson();
    } catch (UnirestException e) {
      e.printStackTrace();
    }
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