package com.github.kkanzelmeyer.alfred.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.plugins.WebcamMotionPlugin;

public class DatamodelTest
{

  private static final Logger LOG = LoggerFactory.getLogger(DatamodelTest.class);

  /**
   * Simple data model test. Inserts a new object into the data model, then
   * creates a copy from fetching the object, then compares the two
   */
  @Test
  public void dataModelTest()
  {
    LOG.debug("Data Model Test");
    LOG.debug("Creating state device");
    StateDevice doorbell = new StateDevice(new Builder().setId("front-door").setName("Front Door")
        .setState(State.INACTIVE).setType(Type.DOORBELL_WEBCAM).build());

    LOG.debug("Adding device to device manager");
    StateDeviceManager.INSTANCE.addStateDevice(doorbell);

    StateDevice copy = new StateDevice(StateDeviceManager.INSTANCE.getDevice(doorbell.getId()));
    LOG.debug(copy.toString());

    assertEquals("Device management test - devices should be equal", doorbell, copy);
    
    // clean up the state
    StateDeviceManager.INSTANCE.removeStateDevice(doorbell);
  }

  @Test
  public void stateTest()
  {
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
    WebcamMotionPlugin plugin = new WebcamMotionPlugin(doorbell);
    plugin.activate();

    // set a new state
    LOG.debug("Injecting new device state");
    StateDeviceManager.INSTANCE.updateStateDevice(doorbell.getId(), State.ACTIVE);

    assertEquals("State change test - states should be equal",
        StateDeviceManager.INSTANCE.getDevice(doorbell.getId()).getState(), State.ACTIVE);

    // clean up the state
    plugin.deactivate();
    StateDeviceManager.INSTANCE.removeStateDevice(doorbell);
  }

}
