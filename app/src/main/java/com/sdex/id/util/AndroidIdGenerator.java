package com.sdex.id.util;

import java.util.Random;

public class AndroidIdGenerator {

  public static String generateRandomAndroidId() {
    StringBuilder randomString = new StringBuilder();
    String st = "12AB345CD67890EF";
    Random rn = new Random();
    for (int i = 0; i < 16; i++) {
      randomString.append(st.charAt(rn.nextInt(st.length())));
    }
    return randomString.toString();
  }
}
