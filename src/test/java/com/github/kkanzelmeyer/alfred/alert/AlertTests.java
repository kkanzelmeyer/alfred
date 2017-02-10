package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.server._Config;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;

import static org.junit.Assert.assertTrue;

/**
 * Created by kevinkanzelmeyer on 2/9/17.
 */
public class AlertTests {

  @Test
  public void firebaseAlert() {
    try {
      // load the api key
      File file = new File(getClass()
                               .getClassLoader().getResource("config.json").getFile());
      _Config config = _Config.createConfig(file.getAbsolutePath());
      Server.INSTANCE.setConfig(config);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
    FirebaseAlert alert = new FirebaseAlert();
    assertTrue(alert.sendAlert("hello moto"));
  }
}
