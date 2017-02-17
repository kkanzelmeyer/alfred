package com.github.kkanzelmeyer.alfred.storage;

import com.github.kkanzelmeyer.alfred.datastore.Store;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class FirebaseFileStorage implements IStorageService {

  private final Logger logger = LoggerFactory.getLogger(FirebaseFileStorage.class);
  private final ServiceType type = ServiceType.FIREBASE;
  private Storage storage = null;

  @Override
  public void setup() {
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
  }

  @Override
  public String saveImage(BufferedImage img) {
    try {
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ImageIO.write(img, "jpg", baos);
      byte[] bytes = baos.toByteArray();
      String bucketName = Store.INSTANCE.getConfig().bucket;
      // save in a test directory if its a test environment
      if (Store.INSTANCE.getConfig().environment.equals("development")) {
        bucketName += "/test";
      }
      BlobId blobId = BlobId.of(bucketName, StorageBridge.INSTANCE.getFileName());
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
      // create the blob in one request.
      return storage.create(blobInfo, bytes).getSelfLink();
    } catch (Exception e) {
      logger.error("error saving file", e);
    }
    return null;
  }

  @Override
  public ServiceType getType() {
    return type;
  }

}
