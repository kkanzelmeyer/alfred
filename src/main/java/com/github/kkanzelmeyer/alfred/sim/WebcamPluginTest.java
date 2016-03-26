package com.github.kkanzelmeyer.alfred.sim;

public class WebcamPluginTest
{
//
//  public static final Logger LOG = LoggerFactory.getLogger(WebcamPluginTest.class);
//  
//  @Test
//  public void captureImage()
//  {
//    // count number of files in the image directory for later comparison
//    File imgDir = new File(Config.INSTANCE.getImageDir());
//    int oldLength = imgDir.list().length;
//    LOG.debug("{} files are in {}", oldLength, imgDir.getAbsolutePath());
//    
//    // create new device and add it to the data model
//    LOG.debug("State Test");
//    LOG.debug("Creating state device");
//    StateDevice doorbell = new StateDevice(new Builder()
//        .setId("front-door")
//        .setName("Front Door")
//        .setState(State.INACTIVE)
//        .setType(Type.DOORBELL_WEBCAM)
//        .build());
//
//    LOG.debug("Adding device to device manager");
//    StateDeviceManager.INSTANCE.addStateDevice(doorbell);
//
//    LOG.debug("Activating new plugin");
//    RPDoorbellPluginWebcam plugin = new RPDoorbellPluginWebcam(12, doorbell);
//    plugin.activate();
//
//    // trigger the webcam by setting the device to ACTIVE
//    LOG.debug("Injecting new device state");
//    StateDeviceManager.INSTANCE.updateStateDevice(doorbell.getId(), State.ACTIVE);
//
//    // Set the timeout to a low value for testing
//    Config.INSTANCE.setDoorbellReset(1000);
//    // reset to INACTIVE
//    try
//    {
//      Thread.sleep(15000);
//    }
//    catch (InterruptedException e)
//    {
//      LOG.error("Problem sleeping", e);
//    }
//
//    // deactivate the plugin to cancel the timing thread
//    LOG.debug("Deactivating plugin " + plugin.getClass().getSimpleName());
//    plugin.deactivate();
//    
//    // count the number of files in the image directory again and compare
//    // against previous count
//    int newLength = new File(Config.INSTANCE.getImageDir()).list().length;
//    LOG.debug("{} files are in {}", newLength, Config.INSTANCE.getImageDir());
//    assertTrue("This is not a good test", true);
//    
//  }

}
