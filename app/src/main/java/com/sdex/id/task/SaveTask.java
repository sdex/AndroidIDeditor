package com.sdex.id.task;

import android.arch.lifecycle.MutableLiveData;
import android.os.AsyncTask;

import com.sdex.id.ProgressViewModel;
import com.sdex.id.xml.SettingsFile;
import com.sdex.id.xml.SettingsItem;
import com.sdex.id.xml.XmlFileWriter;

public class SaveTask extends AsyncTask<Void, Integer, Void> {

  private final ProgressViewModel viewModel;
  private final MutableLiveData<SettingsFile> liveData;
  private final SettingsFile settingsFile;
  private final String packageName;
  private final String newId;
  private final String newDefaultId;

  public SaveTask(ProgressViewModel viewModel, MutableLiveData<SettingsFile> liveData,
                  SettingsFile settingsFile, String packageName, String newId, String newDefaultId) {
    this.viewModel = viewModel;
    this.liveData = liveData;
    this.settingsFile = settingsFile;
    this.packageName = packageName;
    this.newId = newId.toUpperCase();
    this.newDefaultId = newDefaultId;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    publishProgress(ProgressViewModel.PROGRESS_START);
    if (settingsFile != null) {
      for (SettingsItem item : settingsFile.getItems()) {
        if (item.getPackageName().equals(packageName)) {
          item.setValue(newId);
          item.setDefaultValue(newDefaultId);
          break;
        }
      }
      XmlFileWriter.write(settingsFile);
    }
    liveData.postValue(settingsFile);
    publishProgress(ProgressViewModel.PROGRESS_FINISH);
    return null;
  }

  @Override
  protected void onProgressUpdate(Integer... values) {
    super.onProgressUpdate(values);
    viewModel.getProgress().setValue(values[0]);
  }
}