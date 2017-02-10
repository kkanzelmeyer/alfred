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

import com.github.kkanzelmeyer.alfred.datastore.AlfredStore;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class FirebaseFileStorage implements IStorageService {

  private final Logger logger = LoggerFactory.getLogger(FirebaseFileStorage.class);
  private final ServiceType type = ServiceType.FIREBASE;
  private Storage storage = null;

  @Override
  public void setup() {
    File file = new File(AlfredStore.INSTANCE.getConfig().getAuthFilePath());
    InputStream inputStream;

    try {
      inputStream = new FileInputStream(file);
      storage = StorageOptions.newBuilder().setProjectId("alfred-d5f8a")
          .setCredentials(ServiceAccountCredentials.fromStream(inputStream)).build().getService();
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
      // logger.debug("Saving file: {}", file.getName());
      // byte[] bytes = Files.readAllBytes(file.toPath());

      String bucketName = "alfred-d5f8a.appspot.com";
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