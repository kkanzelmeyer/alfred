package com.github.kkanzelmeyer.alfred.storage;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class LocalStorage implements IStorageService {
  
  private Logger logger = LoggerFactory.getLogger(LocalStorage.class);
  private final ServiceType type = ServiceType.LOCAL;
  
  public void setup() {

  }
  
  public String saveImage(BufferedImage image)
  {
    // Save the image to a file
    try
    {
      // Create directory
      Calendar today = Calendar.getInstance();
      DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
      String dayFormat = df.format(today.getTime());
      File directory = new File(Store.INSTANCE.getConfig().imageDir + "/" + dayFormat);
      if(!directory.exists())
      {
        directory.mkdirs();
      }
      
      // Create file
      String filename = StorageBridge.INSTANCE.getFileName();
      File outputfile = new File(directory, filename);
      logger.debug("Saving image: {}", outputfile.getAbsolutePath());
      ImageIO.write(image, "jpg", outputfile);
      logger.debug("Image saved: {}", outputfile.getAbsolutePath());
      return outputfile.getAbsolutePath();
    }
    catch (Exception e)
    {
      logger.error("Trouble saving image", e);
    }
    return null;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

}
