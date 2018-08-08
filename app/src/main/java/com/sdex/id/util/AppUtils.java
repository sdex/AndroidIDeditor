package com.sdex.id.util;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppUtils {

  public static void openApplicationInfo(Context context, String packageName) {
    try {
      Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
      intent.setData(Uri.parse("package:" + packageName));
      context.startActivity(intent);
    } catch (ActivityNotFoundException e) {
      Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_APPLICATIONS_SETTINGS);
      context.startActivity(intent);
    }
  }
}
