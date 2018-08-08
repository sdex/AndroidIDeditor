package com.sdex.id.task;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.sdex.id.xml.SettingsFile;
import com.sdex.id.xml.SettingsItem;

import java.util.List;

public class AppNameFetcher {

  private final PackageManager packageManager;

  public AppNameFetcher(PackageManager packageManager) {
    this.packageManager = packageManager;
  }

  public void addAppNames(@NonNull SettingsFile settingsFile) {
    List<SettingsItem> items = settingsFile.getItems();
    if (items != null) {
      for (SettingsItem item : items) {
        item.setAppName(getAppName(item.getPackageName()));
      }
    }
  }

  private String getAppName(String packageName) {
    try {
      ApplicationInfo info = packageManager.getApplicationInfo(packageName, 0);
      CharSequence label = packageManager.getApplicationLabel(info);
      return label.toString();
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }
}
