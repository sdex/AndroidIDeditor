package com.sdex.id;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.sdex.id.util.SingleLiveEvent;

public class ProgressViewModel extends AndroidViewModel {

  public static final int PROGRESS_START = 0;
  public static final int PROGRESS_FINISH = 1;

  private SingleLiveEvent<Integer> liveData;

  public ProgressViewModel(@NonNull Application application) {
    super(application);
  }

  public MutableLiveData<Integer> getProgress() {
    if (liveData == null) {
      liveData = new SingleLiveEvent<>();
    }
    return liveData;
  }
}
