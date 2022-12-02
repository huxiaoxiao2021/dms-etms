package com.jd.bluedragon.distribution.jy.enums;

public enum ExcepScanTypeEnum {
  INTERCEPTE(1,"拦截");
  private Integer code;
  private String name;

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  ExcepScanTypeEnum(Integer code, String name) {
    this.code = code;
    this.name = name;
  }
}
