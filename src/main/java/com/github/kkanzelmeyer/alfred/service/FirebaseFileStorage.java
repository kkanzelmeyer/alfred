package com.github.kkanzelmeyer.alfred.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.datastore.AlfredStore;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class FirebaseFileStorage implements IAlfredService {

  private final Logger logger = LoggerFactory.getLogger(FirebaseFileStorage.class);
  private Storage storage = null;

  @Override
  public void setup() {
    File file = new File(AlfredStore.INSTANCE.getConfig().getAuthFilePath());
    InputStream inputStream;

    try {
      inputStream = new FileInputStream(file);
      storage = StorageOptions.newBuilder()
          .setProjectId("alfred-d5f8a")
          .setCredentials(ServiceAccountCredentials.fromStream(inputStream))
          .build()
          .getService();
    } catch (IOException e) {
      logger.error("error reading auth file", e);
    }
  }
  
  public void saveImage(File file) {
    try {
      logger.debug("Saving file: {}", file.getName());
      byte[] bytes = Files.readAllBytes(file.toPath());

      String bucketName = "alfred-d5f8a.appspot.com";
      BlobId blobId = BlobId.of(bucketName, file.getName());
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("image/jpeg").build();
      // create the blob in one request.
      storage.create(blobInfo, bytes);
    } catch (Exception e) {
      logger.error("error saving file", e);
    }

  }

}
