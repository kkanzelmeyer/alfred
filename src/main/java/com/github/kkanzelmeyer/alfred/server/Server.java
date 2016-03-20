package com.github.kkanzelmeyer.alfred.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.RPDoorbellPluginWebcam;

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

  public void sendTextAlert()
  {
    LOG.trace("Sending text alerts!");
  }

}
