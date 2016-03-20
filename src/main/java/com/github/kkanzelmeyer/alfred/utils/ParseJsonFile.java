package com.github.kkanzelmeyer.alfred.utils;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParseJsonFile
{

  final private static Logger LOG = LoggerFactory.getLogger(ParseJsonFile.class);

  public static JSONObject toObject(String filename) throws FileNotFoundException, IOException, ParseException
  {
    LOG.debug("Getting resource file {}", filename);
    ClassLoader classLoader = ParseJsonFile.class.getClassLoader();
    String saps = classLoader.getResource(filename).getFile();
    LOG.debug("Found resource file {}", saps);

    LOG.debug("Parsing config file");
    JSONParser parser = new JSONParser();
    FileReader fr = new FileReader(saps);

    LOG.debug("Converting to json object");
    Object sapsObj = parser.parse(fr);
    JSONObject json = (JSONObject) sapsObj;
    return json;
  }
}
