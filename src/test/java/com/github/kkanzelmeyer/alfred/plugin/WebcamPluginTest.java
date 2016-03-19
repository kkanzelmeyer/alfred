package com.github.kkanzelmeyer.alfred.plugin;

import java.io.File;

import static org.junit.Assert.*;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.RPDoorbellPluginWebcam;
import com.github.kkanzelmeyer.alfred.server.Config;

public class WebcamPluginTest
{

  public static final Logger LOG = LoggerFactory.getLogger(WebcamPluginTest.class);
  
  @Test
  public void captureImage()
  {
    // count number of files in the image directory for later comparison
    int oldLength = new File(Config.INSTANCE.getImageDir()).list().length;
    LOG.debug("{} files are in {}", oldLength, Config.INSTANCE.getImageDir());
    
    // create new device and add it to the data model
    LOG.debug("State Test");
    LOG.debug("Creating state device");
    StateDevice doorbell = new StateDevice(new Builder()
        .setId("front-door")
        .setName("Front Door")
        .setState(State.INACTIVE)
        .setType(Type.DOORBELL_WEBCAM)
        .build());

    LOG.debug("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);

    LOG.debug("Activating new plugin");
    RPDoorbellPluginWebcam plugin = new RPDoorbellPluginWebcam(12, doorbell);
    plugin.activate();

    // trigger the webcam by setting the device to ACTIVE
    LOG.debug("Injecting new device state");
    StateDeviceManager.INSTANCE.updateStateDevice(doorbell.getId(), State.ACTIVE);

    // wait a few seconds for the webcam to complete and for the device to
    // reset to INACTIVE
    try
    {
      Thread.sleep(7000);
    }
    catch (InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }

    // deactivate the plugin to cancel the timing thread
    LOG.debug("Deactivating plugin " + plugin.getClass().getSimpleName());
    plugin.deactivate();
    
    // count the number of files in the image directory again and compare
    // against previous count
    int newLength = new File(Config.INSTANCE.getImageDir()).list().length;
    LOG.debug("{} files are in {}", newLength, Config.INSTANCE.getImageDir());
    assertTrue("The number of files should have incremented by 1", (newLength == oldLength + 1));
    
  }

}
