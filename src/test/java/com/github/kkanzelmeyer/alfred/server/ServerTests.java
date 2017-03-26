package com.github.kkanzelmeyer.alfred.server;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.alert.AlertBridge;
import com.github.kkanzelmeyer.alfred.alert.AlfredAlert;
import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.github.kkanzelmeyer.alfred.storage.StorageBridge;
import com.github.kkanzelmeyer.alfred.storage.StorageTests;

/**
 * A test saving a file and sending a notification
 * 
 * @author kevinkanzelmeyer
 *
 */
public class ServerTests
{
  private static Logger LOG = LoggerFactory.getLogger(StorageTests.class);

  public static void main(String[] args) {
    try {
      // load the api key
      ClassLoader classLoader = StorageTests.class.getClassLoader();
      File file = new File(classLoader.getResource("config.json").getFile());
      LOG.info("config file: {}", file.getAbsolutePath());
      Config config = Config.createConfig(file.getAbsolutePath());
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

      // get test image
      BufferedImage originalImage = ImageIO
          .read(new File(classLoader.getResource("kanzelmeyer-software-company.jpg").getFile()));
      String imagePath = StorageBridge.INSTANCE.saveImage(ServiceType.FIREBASE, originalImage);

      AlfredAlert alert = new AlfredAlert.Builder()
          .setType(ServiceType.FIREBASE)
          .setMessage("yo dawg")
          .setImagePath(imagePath)
          .build();
      AlertBridge.INSTANCE.sendAlert(alert);
    } catch (Exception e) {
      LOG.error("Error saving or sending", e);
    }

  }

}
