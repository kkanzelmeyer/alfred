package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.google.api.client.http.*;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FirebaseAlert implements IAlertService {

  private final ServiceType type = ServiceType.FIREBASE;
  private final Logger log = LoggerFactory.getLogger(getClass());

  @Override
  public boolean sendAlert(String imagePath, String msg) {
    return false;
  }

  @Override
  public boolean sendAlert(String msg) {
    HttpRequestFactory requestFactory =
        new NetHttpTransport().createRequestFactory(new HttpRequestInitializer() {
          @Override
          public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(new JacksonFactory()));
          }
        });
    try {

      JsonHttpContent content = new JsonHttpContent(
                                                       new JacksonFactory(), new FirebaseNotification("ABC", new Noty("Kevin", 30)));
      log.info("content: {}", content.getData());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType("application/json");
      headers.setAuthorization("key=" + Server.INSTANCE.getConfig().getFcmServerKey());
      headers.setAccept("application/json");
      log.info("auth: {}", headers.getAuthorization());
      HttpRequest request = requestFactory.buildPostRequest(
          new GenericUrl("https://fcm.googleapis.com/fcm/send"), content);
      log.info(request.toString());
      request.setHeaders(headers);
      log.info("response: {}", request.execute().parseAsString());
    } catch (Exception e) {
      log.error("Error sending firebase alert", e);
      return false;
    }
    return true;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

  public class FirebaseNotification {
    @Key
    private String to;
    @Key
    private Noty data;

    public FirebaseNotification(String to, Noty data) {
      this.to = to;
      this.data = data;
    }

    public String toString() {
      return "\nto:\t" + to + "\ndata:\n" + data;
    }
  }

  public class Noty {
    @Key
    private String name;

    @Key
    private int age;

    public Noty(String name, int age) {
      this.name = name;
      this.age = age;
    }

    @Override
    public String toString() {
      return "\tname: " + name + "\n\tage: " + age;
    }
  }

}