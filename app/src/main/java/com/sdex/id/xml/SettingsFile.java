package com.sdex.id.xml;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

@Root(name = "settings")
public class SettingsFile {

  @Attribute
  private String version;
  @ElementList(entry = "setting", inline = true)
  private List<SettingsItem> items;

  public String getVersion() {
    return version;
  }

  public void setVersion(String version) {
    this.version = version;
  }

  public List<SettingsItem> getItems() {
    return items;
  }

  public void setItems(List<SettingsItem> items) {
    this.items = items;
  }
}
