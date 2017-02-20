package com.github.kkanzelmeyer.alfred.alert;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpContent;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpContent;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

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
    if (Store.INSTANCE.getConfig().fcmServerKey.equals("")) {
      throw new IllegalStateException(
          "FCM Server Key has not been set or is not recognized: " + Store.INSTANCE.getConfig().fcmServerKey);
    }
  }

  @Override
  public boolean sendAlert(String imagePath, String msg) {
    log.debug("sendAlert - starting thread for firebase alert");
    FirebaseNotificationSender sender = new FirebaseNotificationSender(imagePath, msg);
    Thread alertThread = new Thread(sender);
    alertThread.start();
    log.debug("sendAlert - thread started, returning");
    return true;
  }

  /**
   * 
   * @return
   */
  private HttpHeaders getHeaders() {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType("application/json");
    headers.setAuthorization("key=" + Store.INSTANCE.getConfig().fcmServerKey);
    headers.setAccept("application/json");
    return headers;
  }

  /**
   * 
   * @param to
   * @param imagePath
   * @param msg
   * @return
   */
  private JsonHttpContent createContent(String to, String imagePath, String msg) {
    JsonHttpContent content = new JsonHttpContent(new JacksonFactory(), new FirebaseNotification(to,
        new NotificationPayload("New Visitor", "Someone's at the door!", "fcm.ACTION.VISITOR"),
        new DataPayload(imagePath, msg)));
    return content;
  }

  /**
   * 
   * @param content
   * @return
   * @throws IOException
   */
  private HttpRequest createRequest(HttpContent content) throws IOException {
    HttpRequest request = requestFactory.buildPostRequest(new GenericUrl("https://fcm.googleapis.com/fcm/send"),
        content);
    log.info(request.toString());
    HttpHeaders headers = getHeaders();
    log.info("auth: {}", headers.getAuthorization());
    request.setHeaders(headers);
    return request;
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
    public NotificationPayload notification;
    
    @Key
    public DataPayload data;

    public FirebaseNotification(String to, NotificationPayload notification, DataPayload data) {
      this.to = to;
      this.notification = notification;
      this.data = data;
    }

    public String toString() {
      return "\nto:\t" + to + "\nnotification:\n" + notification;
    }
  }

  public class NotificationPayload {
    @Key
    public String title;
    
    @Key
    public String body;
    
    @Key
    public String sound = "default";
    
    @Key
    public String tag = "alfred-visitor";

    @Key
    public String click_action;
    
    public NotificationPayload(String title, String body, String click) {
      this.title = title;
      this.body = body;
      this.click_action = click;
    }
    
    @Override
    public String toString() {
      return "\ttitle: " + title + "\n\tbody: " + body;
    }
  }
  
  public class DataPayload {
    @Key
    public String imagePath;

    @Key
    public String message;


    public DataPayload(String path, String msg) {
      this.imagePath = path;
      this.message = msg;
    }

    @Override
    public String toString() {
      return "\timagePath: " + imagePath;
    }
  }

  @Override
  public boolean sendAlert(String msg) {
    // TODO Auto-generated method stub
    return false;
  }

  private class FirebaseNotificationSender implements Runnable {

    private String imagePath;
    private String msg;

    public FirebaseNotificationSender(String imgPath, String msg) {
      this.imagePath = imgPath;
      this.msg = msg;
    }

    @Override
    public void run() {
      try {
        log.debug("run - sending alert for image {}", imagePath);
        String to = Store.INSTANCE.getConfig().deviceTokens.get(0);
        JsonHttpContent content = createContent(to, imagePath, msg);
        log.trace("content: {}", content.getData());
        HttpRequest request = createRequest(content);
        log.trace("headers", request.getHeaders());
        String response = request.execute().parseAsString();
        log.trace("response: {}", response);
        log.debug("finished");
      } catch (Exception e) {
        log.error("Error sending firebase alert", e);
      }
    }

  }

}