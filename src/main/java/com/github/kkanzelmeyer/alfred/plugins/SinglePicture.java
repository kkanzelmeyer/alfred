package com.github.kkanzelmeyer.alfred.plugins;

import java.awt.Dimension;
import java.awt.image.RenderedImage;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class SinglePicture implements Runnable
{

  private RenderedImage  image;
  private WebCamCallback _handler;

  public RenderedImage getImage()
  {
    return image;
  }

  public SinglePicture(WebCamCallback handler)
  {
    _handler = handler;
    Webcam.setDriver(new V4l4jDriver()); // this is important
  }

  public void run()
  {
    // Take a picture
    takePicture();

    // notify handler
    _handler.onComplete(getImage());
  }

  /**
   * Take Picture method uses com.github.sarxos.webcam library to take a picture
   * using the default webcam (video0). Custom dimensions can be defined in the
   * myResolution array. When the webcam has completed taking a picture the
   * image is saved to the specified directory
   */
  public void takePicture()
  {
    // TODO make webcam device a SAP
    Webcam webcam = Webcam.getDefault();
    // Custom resolution
    Dimension[] myResolution = new Dimension[]
    {
        new Dimension(640, 360),
        new Dimension(1280, 720)
    };
    webcam.setCustomViewSizes(myResolution);
    webcam.setViewSize(myResolution[0]);
    // take a dummy picture and discard
    webcam.open();
    webcam.getImage();
    webcam.close();
    
    webcam.open();
    image = webcam.getImage();
    webcam.close();
  }
}
