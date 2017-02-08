package com.github.kkanzelmeyer.alfred.storage;

import java.awt.image.BufferedImage;

import com.github.kkanzelmeyer.alfred.IAlfredService;

public interface IStorageService extends IAlfredService {

  String saveImage(BufferedImage img);

  ServiceType getType();
}
