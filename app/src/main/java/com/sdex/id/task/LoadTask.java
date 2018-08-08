package com.sdex.id.task;

import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageManager;
import android.os.AsyncTask;

import com.sdex.id.util.Logger;
import com.sdex.id.util.Util;
import com.sdex.id.xml.FileReader;
import com.sdex.id.xml.SettingsFile;
import com.topjohnwu.superuser.Shell;
import com.topjohnwu.superuser.io.SuFile;
import com.topjohnwu.superuser.io.SuFileInputStream;

import java.io.InputStream;

import static com.sdex.id.xml.FileReader.FILE_PATH;

public class LoadTask extends AsyncTask<Void, Void, Void> {

  private final PackageManager packageManager;
  private final MutableLiveData<SettingsFile> liveData;
  private final String backupFilePath;

  public LoadTask(PackageManager packageManager, MutableLiveData<SettingsFile> liveData,
                  String backupFilePath) {
    this.packageManager = packageManager;
    this.liveData = liveData;
    this.backupFilePath = backupFilePath;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    Logger.append("===LOAD FILE===");
    Logger.append("ROOT access (Shell.rootAccess()): " + Shell.rootAccess());
    Logger.append("ROOT access (checkSu): " + Util.checkSu());
    SettingsFile settingsFile = null;
    try {
      if (!Util.exists(backupFilePath)) {
        Logger.append("Creating backup file...");
        Util.copyFile(FILE_PATH, backupFilePath);
      }

      SuFile sourceFile = new SuFile(FILE_PATH);
      try (InputStream in = new SuFileInputStream(sourceFile)) {
        Logger.append("Reading data...");
        settingsFile = FileReader.readFile(in);
        Logger.append("Read " + settingsFile.getItems().size() + " items");
      } catch (Exception e) {
        e.printStackTrace();
        Logger.append("Failed to read data: " + e.getMessage());
      }

      if (settingsFile != null) {
        AppNameFetcher appNameFetcher = new AppNameFetcher(packageManager);
        appNameFetcher.addAppNames(settingsFile);
        Logger.append("Successfully read applications");
      }
    } catch (Exception e) {
      e.printStackTrace();
      Logger.append("Unhandled exception: " + e.getMessage());
    }
    Logger.append("Updating UI...");
    liveData.postValue(settingsFile);
    Logger.append("===LOAD FILE END===");
    return null;
  }
}