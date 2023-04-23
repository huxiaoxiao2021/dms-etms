package com.jd.bluedragon.distribution.jy.enums;

public enum SpotBizTypeEnum {
  SPOT_CHECK_TYPE_C(0, "C网"),
  SPOT_CHECK_TYPE_B(1, "B网");
  private Integer code;
  private String name;

  SpotBizTypeEnum(Integer code, String name) {
    this.code = code;
    this.name = name;
  }

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
}
