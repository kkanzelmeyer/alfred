package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.request.HttpRequestWithBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirebaseAlert implements IAlertService {

  private final ServiceType type = ServiceType.FIREBASE;
  private final Logger logger = LoggerFactory.getLogger(getClass());
  
  @Override
  public boolean sendAlert(String imagePath, String msg) {
    return false;
  }

  @Override
  public boolean sendAlert(String msg) {
    HttpRequestWithBody request = null;
    try {
      request = Unirest.post("https://fcm.googleapis.com/fcm/send");
      request.header("accept", "application/json");
      request.header("Content-Type", "application/json");
      request.header("Authorization", "key=" + Server.INSTANCE.getConfig().getFcmServerKey());
      request.body("{\"parameter\":\"value\", \"foo\":\"bar\"}");
      logger.info("json request: {}", request);

      HttpResponse<String> jsonResponse = request.asString();
      logger.info("json response: {}, {}, {}",
          jsonResponse.getStatusText(),
          jsonResponse.getBody().toCharArray(),
          jsonResponse.getStatus());
    } catch (Exception e) {
      logger.error("Error sending firebase alert", e);
    }
    return true;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

}