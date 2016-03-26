package com.github.kkanzelmeyer.alfred.plugins;

import java.awt.Dimension;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.server.Config;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class MultiPicture implements Runnable
{
  private WebCamCallback      mHandler;
  private int                 mImages = 0;
  private static final Logger LOG     = LoggerFactory.getLogger(MultiPicture.class);

  public MultiPicture(int num, WebCamCallback handler)
  {
    mHandler = handler;
    mImages = num;
    Webcam.setDriver(new V4l4jDriver()); // this is important
  }

  public void run()
  {
    // Take a picture
    takePictures();

    // notify handler
    mHandler.onComplete();
  }

  /**
   * Take Picture method uses com.github.sarxos.webcam library to take a picture
   * using the default webcam (video0). Custom dimensions can be defined in the
   * myResolution array. When the webcam has completed taking a picture the
   * image is saved to the specified directory
   */
  public void takePictures()
  {
    // TODO make webcam device a SAP
    Webcam webcam = Webcam.getDefault();
    webcam.open();
    // Custom resolution
    Dimension[] myResolution = new Dimension[]
    {
        new Dimension(640, 360),
        new Dimension(1280, 720)
    };
    webcam.setCustomViewSizes(myResolution);
    webcam.setViewSize(myResolution[0]);
    for (int i = 0; i < mImages; i++)
    {
      saveImage(webcam.getImage());
    }
    webcam.close();
  }

  private boolean saveImage(RenderedImage img)
  {
    LOG.debug("Finished taking picture. Adding to message");

    // Save the image to a file
    LOG.debug("Saving image file on server");
    String date = String.valueOf(System.currentTimeMillis());
    String filename = "visitor" + date + ".jpg";
    try
    {
      File directory = new File(Config.INSTANCE.getImageDir());
      File outputfile = new File(directory, filename);
      ImageIO.write(img, "jpg", outputfile);
      LOG.debug("Image saved: {}", outputfile.getAbsolutePath());
      return true;
    }
    catch (IOException e)
    {
      LOG.error("Trouble saving image", e);
    }
    return false;
  }
}
