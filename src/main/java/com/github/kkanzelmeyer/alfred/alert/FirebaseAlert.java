package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
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
  HttpRequestFactory requestFactory = null;


  @Override
  public void setup() {
    if (requestFactory == null) {
      requestFactory =
          new NetHttpTransport().createRequestFactory(
              (HttpRequest request) -> request.setParser(new JsonObjectParser(new JacksonFactory()))
          );
    }
  }

  @Override
  public boolean sendAlert(String imagePath, String msg) {
    return false;
  }

  @Override
  public boolean sendAlert(String msg) {
    try {
      String to = Store.INSTANCE.getConfig().deviceTokens.get(0);
      JsonHttpContent content = new JsonHttpContent(
          new JacksonFactory(), new FirebaseNotification(to, new Noty("yo dawg", "wat", "fcm.ACTION.HELLO")));
      log.info("content: {}", content.getData());
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType("application/json");
      headers.setAuthorization("key=" + Store.INSTANCE.getConfig().fcmServerKey);
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
    public String to;

    @Key
    public boolean content_available = true;

    @Key
    public Noty notification;

    public FirebaseNotification(String to, Noty notification) {
      this.to = to;
      this.notification = notification;
    }

    public String toString() {
      return "\nto:\t" + to + "\nnotification:\n" + notification;
    }
  }

  public class Noty {
    @Key
    public String title;

    @Key
    public String body;

    @Key
    public String sound = "default";

    @Key
    public String click_action;

    public Noty(String title, String body, String click) {
      this.title = title;
      this.body = body;
      this.click_action = click;
    }

    @Override
    public String toString() {
      return "\ttitle: " + title + "\n\tbody: " + body;
    }
  }

}