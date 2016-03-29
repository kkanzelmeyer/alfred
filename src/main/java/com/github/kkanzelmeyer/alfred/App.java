package com.github.kkanzelmeyer.alfred;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.WebcamMotionPlugin;
import com.github.kkanzelmeyer.alfred.server.Server;


/**
 * Alfred App
 * @author kevin
 *
 */
public class App
{

  private static final Logger LOG = LoggerFactory.getLogger(App.class);

  public static void main(String[] args)
  {
    Server.INSTANCE.printGreeting();
    
    // create new device and add it to the data model
    LOG.info("Starting Alfred");
    LOG.trace("Creating state device");
    StateDevice doorbell = new StateDevice(new Builder()
        .setId("front-door")
        .setName("Front Door")
        .setState(State.INACTIVE)
        .setType(Type.MOTION_WEBCAM)
        .build());

    LOG.trace("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);

    LOG.trace("Activating new plugin");
    WebcamMotionPlugin plugin = new WebcamMotionPlugin(doorbell);
    plugin.activate();

    while(true) {
      
    }
  }
}
