package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.server.Config;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by kevinkanzelmeyer on 2/9/17.
 */
public class AlertTests {

  public void firebaseAlert() {
    try {
      // load the api key
      File file = new File(getClass()
                               .getClassLoader().getResource("config.json").getFile());
      Config config = Config.createConfig(file.getAbsolutePath());
      Store.INSTANCE.setConfig(config);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    FirebaseAlert alert = new FirebaseAlert();
    assertTrue(alert.sendAlert("hello moto"));
  }
}
