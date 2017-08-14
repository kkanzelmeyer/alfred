package com.github.kkanzelmeyer.alfred.storage;

import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.server.Config;

/**
 * Created by kevinkanzelmeyer on 2/16/17.
 */
public class StorageTests {
  private static Logger logger = LoggerFactory.getLogger(StorageTests.class);

  public static void main(String[] args) {
    try {
      // load the api key
      ClassLoader classLoader = StorageTests.class.getClassLoader();
      File file = new File(classLoader.getResource("config.json").getFile());
      logger.info("config file: {}", file.getAbsolutePath());
      Config config = Config.createConfig(file.getAbsolutePath());
      logger.info("auth file: {}", config.authFilePath);
      Store.INSTANCE.setConfig(config);

      // set up storage service
      for (String service : Store.INSTANCE.getConfig().storageServices) {
        StorageBridge.INSTANCE.addService(service);
      }
      StorageBridge.INSTANCE.setup();

      // get test image
      // BufferedImage originalImage = ImageIO
      // .read(new
      // File(classLoader.getResource("kanzelmeyer-software-company.jpg").getFile()));
      // Map<ServiceType, String> result =
      // StorageBridge.INSTANCE.saveImage(originalImage);
      // for (Entry<ServiceType, String> entry : result.entrySet()) {
      // logger.info("{}, {}", entry.getKey(), entry.getValue());
      // }
    } catch (Exception e) {
      logger.error("error saving file", e);
    }

  }
}
