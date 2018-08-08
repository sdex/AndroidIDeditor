package com.sdex.id.xml;

import com.sdex.id.util.RootUtils;

public class XmlFileWriter {

  private static final String header =
    "<?xml version='\\''1.0'\\'' encoding='\\''UTF-8'\\'' standalone='\\''yes'\\'' ?>\n";

  public static void write(SettingsFile settingsFile) {
    StringBuilder builder = new StringBuilder();
    builder.append(header);
    builder.append("<settings version=\"-1\">\n");
    for (SettingsItem item : settingsFile.getItems()) {
      builder.append(item.toXmlString());
    }
    builder.append("</settings>");
    execute(builder.toString());
  }

  private static void execute(String value) {
    final String command = "echo '" + value + "' > /data/system/users/0/settings_ssaid.xml";
    RootUtils.execute(command);
  }

}
