package com.github.kkanzelmeyer.alfred.plugins;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datamodel.StateDevice;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceHandler;
import com.github.kkanzelmeyer.alfred.datamodel.StateDeviceManager;
import com.github.kkanzelmeyer.alfred.datamodel.enums.State;
import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.github.kkanzelmeyer.alfred.storage.ServiceType;
import com.github.kkanzelmeyer.alfred.storage.StorageBridge;
import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamMotionDetector;
import com.github.sarxos.webcam.WebcamMotionDetectorDefaultWithDNE;
import com.github.sarxos.webcam.WebcamMotionEvent;
import com.github.sarxos.webcam.WebcamMotionListener;
import com.github.sarxos.webcam.ds.v4l4j.V4l4jDriver;

public class WebcamMotionPlugin extends DevicePlugin {

  private DoorbellStateHandler stateHandler = null;
  private final String CLASSNAME = this.getClass().getSimpleName();
  private Timer timer = null;
  private Webcam webcam = null;
  private WebcamMotionDetector detector = null;
  private BufferedImage detectionImage = null;
  private final Logger log = LoggerFactory.getLogger(WebcamMotionPlugin.class);

  public WebcamMotionPlugin(StateDevice device) {
    super(device);
    try {
      log.debug("Creating webcam instance");
      Webcam.setDriver(new V4l4jDriver()); // this is important
      webcam = Webcam.getDefault();
      Dimension[] myResolution = new Dimension[] { new Dimension(640, 360), new Dimension(1280, 720) };
      webcam.setCustomViewSizes(myResolution);
      webcam.setViewSize(myResolution[0]);
      // take a throwaway picture to allow the camera to adjust itself
      webcam.open();
      webcam.getImage();
      webcam.close();
    } catch (Exception e) {
      log.error("Error creating webcam plugin", e);
    }
  }

  @Override
  public void activate() {
    log.debug("Activating plugin: {}", CLASSNAME);

    // motion detection
    if (detector == null) {
      detector = new WebcamMotionDetector(webcam,
          new WebcamMotionDetectorDefaultWithDNE(Store.INSTANCE.getConfig().pixelThreshold,
              Store.INSTANCE.getConfig().areaThreshold),
          Store.INSTANCE.getConfig().motionInterval);
      detector.setMaxAreaThreshold(Store.INSTANCE.getConfig().maxAreaThreshold);
      detector.setInertia(Store.INSTANCE.getConfig().inertia);
      detector.addMotionListener(new MotionListener());
      // create do-not-engage zone
      detector.setDne(Store.INSTANCE.getConfig().dne);
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
    log.trace("Deactivating plugin {}", CLASSNAME);
    StateDeviceManager.INSTANCE.removeDeviceHandler(stateHandler);
    stateHandler = null;
    detector.stop();
  }

  private class MotionListener implements WebcamMotionListener {

    @Override
    public void motionDetected(WebcamMotionEvent wme) {
      log.debug("Motion detected");
      StateDevice device = StateDeviceManager.INSTANCE.getDevice(myDeviceId);
      State newState;
      detectionImage = wme.getCurrentImage();

      // store remotely and send alert only when the state changes
      if (device.getState() == State.INACTIVE) {
        // String imagePath =
        // StorageBridge.INSTANCE.saveImage(ServiceType.FIREBASE,
        // detectionImage);

        // calculate / get motion metrics
        int sum = 0;
        for (int t : wme.getSource().getThresholds()) {
          sum += t;
        }
        double avgThreshold = sum / wme.getSource().getThresholds().size();
        log.debug("Average Threshold: {}", avgThreshold);
        // String area = "Area affected by motion: " + wme.getArea();
        // String threshold = "Average threshold: " + avgThreshold;
        // String cog = "Motion COG: " + wme.getCog();
        // String message = area + "<br/>" + threshold + "<br/>" + cog;
        // AlfredAlert alert = new AlfredAlert.Builder()
        // .setType(ServiceType.FIREBASE)
        // .setMessage(message)
        // .setImagePath(imagePath)
        // .build();
        // AlertBridge.INSTANCE.sendAlert(alert);
        newState = State.ACTIVE;
        StateDeviceManager.INSTANCE.updateStateDevice(myDeviceId, newState);
      }

      log.debug("Area affected by motion: {}%", wme.getArea());
      log.debug("Motion COG: {}%", wme.getCog());

      // save every detection instance locally
      StorageBridge.INSTANCE.saveImage(ServiceType.LOCAL, detectionImage);
    }

  }

  private class DoorbellStateHandler implements StateDeviceHandler {
    @Override
    public void onAddDevice(StateDevice device) {
    }

    @Override
    public void onUpdateDevice(StateDevice device) {
      log.debug("State update received by {}", CLASSNAME);
      if (device.getState() == State.ACTIVE) {
        log.debug("State set to {}", device.getState());
        startResetTimer(device);
      } else {
        log.trace("Resetting status");
      }
    }

    @Override
    public void onRemoveDevice(StateDevice device) {
    }

    public void startResetTimer(StateDevice device) {
      Calendar calendar = Calendar.getInstance();
      calendar.add(Calendar.SECOND, Store.INSTANCE.getConfig().doorbellReset);
      Date endTime = calendar.getTime();
      log.info("Scheduling reset timer");
      // timer.schedule(new DoorbellResetTask(device), endTime);
      timer.schedule(new TimerTask() {
        @Override
        public void run() {
          log.info("Resetting {}", device.getName());
          StateDeviceManager.INSTANCE.updateStateDevice(device.getId(), State.INACTIVE);
        }

      }, endTime);
    }
  }
}
