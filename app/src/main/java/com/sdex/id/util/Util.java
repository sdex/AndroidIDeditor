package com.sdex.id.util;

import com.topjohnwu.superuser.ShellUtils;
import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;
import com.topjohnwu.superuser.io.SuFileOutputStream;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Util {

  public static boolean exists(String file) {
    SuFile suFile = new SuFile(file);
    return suFile.exists();
  }

  public static void copyFile(String source, String destination) {
    SuFile sourceFile = new SuFile(source);
    if (sourceFile.exists()) {
      try (InputStream in = new SuFileInputStream(sourceFile);
           OutputStream out = new SuFileOutputStream(destination)) {
        ShellUtils.pump(in, out);
        Logger.append("File copied successfully");
        Logger.append("From: " + source);
        Logger.append("To: " + destination);
      } catch (IOException e) {
        e.printStackTrace();
        Logger.append("Failed to copy file: " + e.getMessage());
      }
      Logger.append("From: " + source);
      Logger.append("To: " + destination);
    }
  }

  public static boolean checkSu() {
    return RootUtils.isSuAvailable();
  }
}
