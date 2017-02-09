package com.github.kkanzelmeyer.alfred.plugins;

import com.github.kkanzelmeyer.alfred.alert.AlertBridge;
import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceHandler;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.server.Server;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.github.kkanzelmeyer.alfred.storage.StorageBridge;
import com.github.sarxos.webcam.*;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.*;

public class WebcamMotionPlugin extends DevicePlugin {

  private DoorbellStateHandler stateHandler = null;
  private final String CLASSNAME = this.getClass().getSimpleName();
  private Timer timer = null;
  private Webcam webcam = null;
  private WebcamMotionDetector detector = null;
  private BufferedImage detectionImage = null;
  // private BufferedImage baselineImage = null;
  private String detectionImagePath = null;
  private static final Logger logger = LoggerFactory.getLogger(WebcamMotionPlugin.class);

  public WebcamMotionPlugin(StateDevice device) {
    super(device);
    // TODO make webcam device a SAP
    logger.debug("Creating webcam instance");
    Webcam.setDriver(new V4l4jDriver()); // this is important
    webcam = Webcam.getDefault();
    Dimension[] myResolution = new Dimension[] { new Dimension(640, 360), new Dimension(1280, 720) };
    webcam.setCustomViewSizes(myResolution);
    webcam.setViewSize(myResolution[0]);
    // take a throwaway picture to allow the camera to adjust itself
    webcam.open();
    webcam.getImage();
    webcam.close();
  }

  @Override
  public void activate() {
    logger.debug("Activating plugin: {}", CLASSNAME);

    // motion detection
    if (detector == null) {
      detector = new WebcamMotionDetector(webcam,
                                             new WebcamMotionDetectorDefaultWithDNE(Server.INSTANCE.getConfig().getPixelThreshold(),
                                                                                       Server.INSTANCE.getConfig().getAreaThreshold()),
                                             Server.INSTANCE.getConfig().getMotionInterval());
      detector.setMaxAreaThreshold(Server.INSTANCE.getConfig().getMaxAreaThreshold());
      detector.setInertia(Server.INSTANCE.getConfig().getInertia());
      detector.addMotionListener(new MotionListener());
      // create do-not-engage zone
      detector.setDne(Server.INSTANCE.getConfig().getDne());
      detector.start();
    }

    // State handler
    if (stateHandler == null) {
      stateHandler = new DoorbellStateHandler();
      StateDeviceManager.INSTANCE.addDeviceHandler(stateHandler);
    }

    // create new timer object for reset task
    timer = new Timer();
  }

  @Override
  public void deactivate() {
    timer.cancel();
    logger.trace("Deactivating plugin {}", CLASSNAME);
    StateDeviceManager.INSTANCE.removeDeviceHandler(stateHandler);
    stateHandler = null;
    detector.stop();
  }

  private class MotionListener implements WebcamMotionListener {

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
      logger.info("Motion detected");
      logger.debug("Area affected by motion: {}%", wme.getArea());
      logger.debug("Motion COG: {}%", wme.getCog());
      int sum = 0;
      for (int t : wme.getSource().getThresholds()) {
        logger.trace("Threshold: {}", t);
        sum += t;
      }
      double avgThreshold = sum / wme.getSource().getThresholds().size();
      logger.debug("Average Threshold: {}", avgThreshold);
      String area = "Area affected by motion: " + wme.getArea();
      String threshold = "Average threshold: " + avgThreshold;
      String cog = "Motion COG: " + wme.getCog();
      StateDevice device = StateDeviceManager.INSTANCE.getDevice(myDeviceId);
      State newState;
      detectionImage = wme.getCurrentImage();
      detectionImagePath = "";
      Map<ServiceType, String> imagePaths = StorageBridge.INSTANCE.saveImage(detectionImage);
      if (device.getState() == State.INACTIVE) {
        AlertBridge.INSTANCE.sendAlert(imagePaths, area + "<br/>" + threshold + "<br/>" + cog);
        newState = State.ACTIVE;
        StateDeviceManager.INSTANCE.updateStateDevice(myDeviceId, newState);
      }
      // baselineImage = wme.getPreviousImage();
      // saveImage(baselineImage);
    }

  }

  private class DoorbellStateHandler implements StateDeviceHandler {
    @Override
    public void onAddDevice(StateDevice device) {
    }

    @Override
    public void onUpdateDevice(StateDevice device) {
      logger.debug("State update received by {}", CLASSNAME);
      if (device.getState() == State.ACTIVE) {
        logger.debug("State set to {}", device.getState());
        startResetTimer(device);
      } else {
        logger.trace("Resetting status");
      }
    }

    @Override
    public void onRemoveDevice(StateDevice device) {
    }

    public void startResetTimer(StateDevice device) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.SECOND, Server.INSTANCE.getConfig().getDoorbellReset());
      Date endTime = calendar.getTime();
      logger.info("Scheduling reset timer");
      // timer.schedule(new DoorbellResetTask(device), endTime);
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          logger.info("Resetting {}", device.getName());
          StateDeviceManager.INSTANCE.updateStateDevice(device.getId(), State.INACTIVE);
        }

      }, endTime);
    }
  }
}
