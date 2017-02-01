package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;
import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Config;

public class LocalStorage implements IStorageService {
  
  private Logger logger = LoggerFactory.getLogger(LocalStorage.class);
  
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
      File directory = new File(Config.INSTANCE.getImageDir()+ "/" + dayFormat);
      if(!directory.exists())
      {
        directory.mkdirs();
      }
      
      // Create file
      df = new SimpleDateFormat("yyyyMMdd-kkmmss");
      String date = df.format(today.getTime());
      String filename = "visitor" + date + ".jpg";
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

}
