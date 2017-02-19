package com.github.kkanzelmeyer.alfred;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.alert.AlertBridge;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice.Builder;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datamodel.enums.Type;
import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.plugins.WebcamMotionPlugin;
import com.github.kkanzelmeyer.alfred.server.Config;
import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.storage.StorageBridge;


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
    try {
      // load the config
      ClassLoader classLoader = App.class.getClassLoader();
      File file = new File(classLoader.getResource("config.json").getFile());
      LOG.info("config file: {}", file.getAbsolutePath());
      Config config;
      config = Config.createConfig(file.getAbsolutePath());
      LOG.info("configuration: {}", config.toJson());
      LOG.info("auth file: {}", config.authFilePath);
      Store.INSTANCE.setConfig(config);

      // set up storage service(s)
      for (String service : Store.INSTANCE.getConfig().storageServices) {
        StorageBridge.INSTANCE.addService(service);
      }
      StorageBridge.INSTANCE.setup();
      // set up alert service(s)
      for (String service : Store.INSTANCE.getConfig().alertServices) {
        AlertBridge.INSTANCE.addService(service);
      }
      AlertBridge.INSTANCE.setup();

      // create new device and add it to the data model
      LOG.info("Creating state device");
      StateDevice doorbell = new StateDevice(new Builder().setId("front-door").setName("Front Door")
          .setState(State.INACTIVE).setType(Type.MOTION_WEBCAM).build());

      LOG.info("Adding device to device manager");
      StateDeviceManager.INSTANCE.addStateDevice(doorbell);

      // activate webcam plugin
      LOG.info("Activating new plugin");
      WebcamMotionPlugin plugin = new WebcamMotionPlugin(doorbell);
      plugin.activate();

      // run
      while (true) {

      }

    } catch (Exception e) {
      LOG.error("An application error has occurred", e);
    }
  }
}
