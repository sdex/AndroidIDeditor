package com.sdex.id.xml;

import org.simpleframework.xml.Attribute;

public class SettingsItem {

  @Attribute
  private String id;
  @Attribute
  private String name;
  @Attribute
  private String value;
  @Attribute(name = "package")
  private String packageName;
  @Attribute
  private String defaultValue;
  @Attribute
  private boolean defaultSysSet;
  @Attribute
  private String tag;
  private transient String appName;

  public String getAppName() {
    return appName;
  }

  public void setAppName(String appName) {
    this.appName = appName;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value.toLowerCase();
  }

  public String getPackageName() {
    return packageName;
  }

  public void setPackageName(String packageName) {
    this.packageName = packageName;
  }

  public String getDefaultValue() {
    return defaultValue;
  }

  public void setDefaultValue(String defaultValue) {
    this.defaultValue = defaultValue.toLowerCase();
  }

  public boolean isDefaultSysSet() {
    return defaultSysSet;
  }

  public void setDefaultSysSet(boolean defaultSysSet) {
    this.defaultSysSet = defaultSysSet;
  }

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public boolean canChange() {
//    return !"android".equals(packageName);
    return true;
  }

  public String toXmlString() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append("  <setting id=\"");
    stringBuilder.append(id);
    stringBuilder.append("\" name=\"");
    stringBuilder.append(name);
    stringBuilder.append("\" value=\"");
    stringBuilder.append(value);
    stringBuilder.append("\" package=\"");
    stringBuilder.append(packageName);
    stringBuilder.append("\" defaultValue=\"");
    stringBuilder.append(defaultValue);
    stringBuilder.append("\" defaultSysSet=\"");
    stringBuilder.append(defaultSysSet);
    stringBuilder.append("\" tag=\"");
    stringBuilder.append(tag);
    stringBuilder.append("\" />\n");
    return stringBuilder.toString();
  }
}
