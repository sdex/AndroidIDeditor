package com.sdex.id.xml;

import android.util.Log;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.InputStream;

public class FileReader {

  private static final String TAG = "FileReader";

  public static final String FILE_PATH = "/data/system/users/0/settings_ssaid.xml";
  public static final String BAK_FILE = "settings_ssaid.xml.bak";
  public static final String LOCAL_FILE = "data.xml";

  public static SettingsFile readFile(String path) {
    File file = new File(path);
    Serializer serializer = new Persister();
    try {
      Log.d(TAG, "readFile: " + path);
      SettingsFile settingsFile = serializer.read(SettingsFile.class, file);
      Log.d(TAG, "version: " + settingsFile.getVersion());
      Log.d(TAG, "total records: " + settingsFile.getItems().size());
      return settingsFile;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  public static SettingsFile readFile(InputStream source) {
    Serializer serializer = new Persister();
    try {
      SettingsFile settingsFile = serializer.read(SettingsFile.class, source);
      Log.d(TAG, "version: " + settingsFile.getVersion());
      Log.d(TAG, "total records: " + settingsFile.getItems().size());
      return settingsFile;
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }
}
