package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;

import com.github.kkanzelmeyer.alfred.service.IAlfredService;

public interface IStorageService extends IAlfredService {

  public void saveImage(BufferedImage img);
}
