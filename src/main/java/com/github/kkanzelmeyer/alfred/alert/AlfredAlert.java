package com.github.kkanzelmeyer.alfred.alert;

import com.github.kkanzelmeyer.alfred.storage.ServiceType;

public class AlfredAlert {

  private ServiceType type;
  private String message;
  private String imagePath;

  public AlfredAlert(Builder builder) {
    this.type = builder.type;
    this.message = builder.message;
    this.imagePath = builder.imagePath;
  }

  public ServiceType getType() {
    return type;
  }

  public String getMessage() {
    return message;
  }

  public String getImagePath() {
    return imagePath;
  }

  public static class Builder {
    private ServiceType type;
    private String message;
    private String imagePath;

    public Builder setType(ServiceType type) {
      this.type = type;
      return this;
    }

    public Builder setMessage(String message) {
      this.message = message;
      return this;
    }

    public Builder setImagePath(String imagePath) {
      this.imagePath = imagePath;
      return this;
    }

    public AlfredAlert build() {
      if (type == null || message == null || imagePath == null) {
        throw new NullPointerException("type, message, and imagepath must be set!");
      }
      return new AlfredAlert(this);
    }

  }

}
