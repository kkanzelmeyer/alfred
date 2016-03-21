package com.github.kkanzelmeyer.alfred.server;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.RPDoorbellPluginWebcam;
import com.twilio.sdk.TwilioRestException;
import com.twilio.sdk.resource.factory.MessageFactory;
import com.twilio.sdk.resource.instance.Message;

public enum Server
{
  INSTANCE;

  private static final Logger LOG = LoggerFactory.getLogger(Server.class);

  /**
   * initialize the state devices and plugins
   */
  public void init()
  {
    LOG.trace("Initializing server");
    
    // create device and add it to the manager
    LOG.trace("Creating state device");
    StateDevice doorbell = new StateDevice(
        new Builder()
        .setId("front-door")
        .setName("Front Door")
        .setState(State.INACTIVE)
        .setType(Type.DOORBELL_WEBCAM)
        .build());

    LOG.trace("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);
    
    // initialize device plugin
    new RPDoorbellPluginWebcam(12, doorbell).activate();

  }

  public void run()
  {
    LOG.trace("Running server");

  }

  public void sendTextAlert(String message, String imagePath) throws TwilioRestException
  {
      // Build a filter for the MessageList
      List<NameValuePair> params = new ArrayList<NameValuePair>();
      params.add(new BasicNameValuePair("Body", message));
      params.add(new BasicNameValuePair("To", "+12563488196"));
      params.add(new BasicNameValuePair("From", "+12562033462")); // TODO SAPS
      params.add(new BasicNameValuePair("MediaUrl", imagePath));
      
      MessageFactory messageFactory = Config.INSTANCE.getTwilioClient().getAccount().getMessageFactory();
      Message msg = messageFactory.create(params);
  }
}
