package com.github.kkanzelmeyer.alfred.storage;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.server.Config;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;

/**
 * Created by kevinkanzelmeyer on 2/16/17.
 */
public class StorageTests {
  private Logger logger = LoggerFactory.getLogger(getClass());

  @Test
  public void saveImageTest() {
    try {
      // load the api key
      ClassLoader classLoader = getClass().getClassLoader();
      File file = new File(classLoader.getResource("config.json").getFile());
      Config config = Config.createConfig(file.getAbsolutePath());
      logger.info("auth path: {}", config.authFilePath);
      Store.INSTANCE.setConfig(config);

      // set up storage service
      for (String service : Store.INSTANCE.getConfig().storageServices) {
        StorageBridge.INSTANCE.addService(service);
      }
      StorageBridge.INSTANCE.setup();

      // get test image
      BufferedImage originalImage = ImageIO.read(new File("src/test/resources/kanzelmeyer-software-company.jpg"));
//      StorageBridge.INSTANCE.saveImage(originalImage);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }
}
