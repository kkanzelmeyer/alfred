package com.github.kkanzelmeyer.alfred.sim;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.WebcamMotionPlugin;

public class WebcamMotionSim
{

  public static final Logger LOG = LoggerFactory.getLogger(WebcamMotionSim.class);
  
  public static void main(String[] args)
  {
    // count number of files in the image directory for later comparison
//    File imgDir = new File(Config.INSTANCE.getImageDir());
//    int oldLength = imgDir.list().length;
//    LOG.debug("{} files are in {}", oldLength, imgDir.getAbsolutePath());
    
    // create new device and add it to the data model
    LOG.debug("Motion Test");
    LOG.debug("Creating state device");
    StateDevice doorbell = new StateDevice(new Builder()
        .setId("front-door")
        .setName("Front Door")
        .setState(State.INACTIVE)
        .setType(Type.MOTION_WEBCAM)
        .build());

    LOG.debug("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);

    LOG.debug("Activating new plugin");
    WebcamMotionPlugin plugin = new WebcamMotionPlugin(doorbell);
    plugin.activate();

    while(true) {
      
    }
    // deactivate the plugin to cancel the timing thread
//    LOG.debug("Deactivating plugin " + plugin.getClass().getSimpleName());
//    plugin.deactivate();
    
  }

}
