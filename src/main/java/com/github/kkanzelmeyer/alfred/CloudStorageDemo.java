package com.github.kkanzelmeyer.alfred;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.kkanzelmeyer.alfred.service.FirebaseFileStorage;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class CloudStorageDemo {

  private static final Logger logger = LoggerFactory.getLogger(FirebaseFileStorage.class);

  public static void saveFile(String filePath) {
    try {
      File file = new File("/Users/kevinkanzelmeyer/.config/gcloud/Alfred-15449af007af.json");
      InputStream inputStream = new FileInputStream(file);

      Storage storage = StorageOptions.newBuilder()
          .setProjectId("alfred-d5f8a")
          .setCredentials(ServiceAccountCredentials.fromStream(inputStream))
          .build()
          .getService();

      Path path = Paths.get(filePath);
      logger.info("Path info: {}", path.getFileName());
      byte[] bytes = Files.readAllBytes(path);

      String bucketName = "alfred-d5f8a.appspot.com";
      BlobId blobId = BlobId.of(bucketName, "squat-depth.jpg");
      BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setContentType("*/*").build();
      // create the blob in one request.
      storage.create(blobInfo, bytes);
    } catch (Exception e) {
      logger.error("error saving file", e);
    }

  }

  public void blobTest() {
    logger.info("Attempting to write to bucket");

    Storage storage;
    try {
      File file = new File("/Users/kevinkanzelmeyer/.config/gcloud/Alfred-15449af007af.json");
      logger.info("File info: {}", file.getAbsolutePath());
      InputStream inputStream = new FileInputStream(file);

      storage = StorageOptions.newBuilder().setProjectId("alfred-d5f8a")
          .setCredentials(ServiceAccountCredentials.fromStream(inputStream)).build().getService();

      String bucketName = "alfred-d5f8a.appspot.com";
      BlobId blobId = BlobId.of(bucketName, "front-door");

      Blob blob = storage.get(blobId);
      if (blob == null) {
        throw new NullPointerException("blob is null :/");
      }

      byte[] prevContent = blob.getContent();
      logger.info("previous content: {}", new String(prevContent, StandardCharsets.UTF_8));
      WritableByteChannel channel = blob.writer();
      channel.write(ByteBuffer.wrap("new content!".getBytes(StandardCharsets.UTF_8)));
      channel.close();

      logger.info("Finished writing to bucket: blob id = {}", blobId);
    } catch (Exception e) {
      logger.error("", e);
    }

  }

  public static void main(String[] args) {
    String filename = "/Users/kevinkanzelmeyer/Documents/squat-img.jpg";
    saveFile(filename);
  }

}
