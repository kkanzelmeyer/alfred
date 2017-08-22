package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import com.google.firebase.database.DatabaseReference;

public class FirebaseFileStorage implements IStorageService {

  private final Logger logger = LoggerFactory.getLogger(FirebaseFileStorage.class);
  private final ServiceType type = ServiceType.FIREBASE;
  private Storage storage = null;
  private DatabaseReference dbRef = null;

  @Override
  public void setup() {
    // set up cloud storage
    File file = new File(Store.INSTANCE.getConfig().authFilePath);
    logger.info("setup - setting up firebase file storage with config : {}", file.getAbsolutePath());
    InputStream inputStream;
    try {
      inputStream = new FileInputStream(file);
      storage = StorageOptions.newBuilder()
          .setProjectId(Store.INSTANCE.getConfig().projectId)
          .setCredentials(ServiceAccountCredentials.fromStream(inputStream))
          .build().getService();
    } catch (IOException e) {
      logger.error("error reading auth file", e);
    }

    // set up database storage
    // try {
    // logger.info("setting up firebase storage");
    // FirebaseOptions options;
    // FileInputStream serviceAccount = new
    // FileInputStream(Store.INSTANCE.getConfig().authFilePath);
    // Map<String, Object> auth = new HashMap<String, Object>();
    // auth.put("uid", "alfred-door-monitor");
    // options = new FirebaseOptions.Builder()
    // .setCredential(FirebaseCredentials.fromCertificate(serviceAccount))
    // .setDatabaseAuthVariableOverride(auth)
    // .setDatabaseUrl("https://" + Store.INSTANCE.getConfig().projectId +
    // ".firebaseio.com/")
    // .build();
    // for (Map.Entry<String, Object> entry :
    // options.getDatabaseAuthVariableOverride().entrySet()) {
    // logger.info("auth variable override: {}, {}", entry.getKey(),
    // entry.getValue());
    // }
    // FirebaseApp.initializeApp(options);
    // dbRef = FirebaseDatabase.getInstance().getReference();
    // logger.info("Firebase database ref successful - {}", dbRef);
    // } catch (Exception e) {
    // logger.error("error initializing firebase database");
    // }
  }

  @Override
  public String saveImage(BufferedImage img) {
    logger.info("saveImage - saving image with firebase");
    String filename = "visitors/" + StorageBridge.INSTANCE.getDate() + "/" + StorageBridge.INSTANCE.getFileName();
    // save in a test directory if its a test environment
    if (Store.INSTANCE.getConfig().environment.equals("development")) {
      filename = "test/" + filename;
    }

    // kick off thread to save the image
    logger.debug("starting thread for file upload");
    FirebaseImageUploader uploader = new FirebaseImageUploader(img, filename);
    Thread uploaderThread = new Thread(uploader);
    uploaderThread.start();
    logger.debug("thread started - returning from saveImage");

    // return the filename
    return filename;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

  /**
   * Runnable to upload an image to firebase
   * 
   * @author kevinkanzelmeyer
   *
   */
  private class FirebaseImageUploader implements Runnable {

    BufferedImage img;
    String filename;

    public FirebaseImageUploader(BufferedImage img, String filename) {
      this.img = img;
      this.filename = filename;
    }

    @Override
    public void run() {
      uploadImage();
      logger.info("save image thread finished");
    }

    private void uploadImage() {
      logger.debug("run - saving image to firebase {}", filename);
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try {
        ImageIO.write(img, "jpg", baos);
      } catch (IOException e) {
        logger.error("Error saving image :/", e);
      }
      byte[] bytes = baos.toByteArray();
      BlobId blobId = BlobId.of(Store.INSTANCE.getConfig().bucket, filename);
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
      // create the blob in one request.
      storage.create(blobInfo, bytes);
      logger.debug("done saving image to firebase {}", filename);
    }

  }

}
