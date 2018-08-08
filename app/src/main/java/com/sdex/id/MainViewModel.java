package com.sdex.id;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;

import com.sdex.id.task.LoadTask;
import com.sdex.id.task.SaveTask;
import com.sdex.id.xml.FileReader;
import com.sdex.id.xml.SettingsFile;

import java.io.File;

public class MainViewModel extends AndroidViewModel {

  private MutableLiveData<SettingsFile> liveData;
  private final String backupFilePath;
  private final PackageManager packageManager;

  public MainViewModel(@NonNull Application application) {
    super(application);
    packageManager = application.getPackageManager();
    File cacheDir = application.getCacheDir();
    backupFilePath = new File(cacheDir, FileReader.BAK_FILE).getAbsolutePath();
  }

  public MutableLiveData<SettingsFile> getPackages() {
    if (liveData == null) {
      liveData = new MutableLiveData<>();
      loadPackages();
    }
    return liveData;
  }

  private void loadPackages() {
    LoadTask loader = new LoadTask(packageManager, liveData, backupFilePath);
    loader.execute();
  }

  public void setAndroidId(ProgressViewModel progressViewModel, SettingsFile settingsFile,
                           String packageName, String newId, String newDefaultId) {
    SaveTask saveTask = new SaveTask(progressViewModel, liveData,
      settingsFile, packageName, newId, newDefaultId);
    saveTask.execute();
  }
}
