package com.jd.bluedragon.enums;

public enum ReadWriteTypeEnum {
  READ("read", "读"),
  WRITE("write", "写");
  private String type;
  private String name;

  ReadWriteTypeEnum(String type, String name) {
    this.type = type;
    this.name = name;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }
}
