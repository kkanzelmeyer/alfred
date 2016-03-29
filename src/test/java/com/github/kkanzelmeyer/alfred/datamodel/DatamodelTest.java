package com.github.kkanzelmeyer.alfred.datamodel;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;

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

}
