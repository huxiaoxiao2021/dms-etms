package com.jd.bluedragon.distribution.jy.enums;

public enum ComboardBarCodeTypeEnum {
  BOX(1, "箱号"),
  PACKAGE(2, "包裹号"),
  WAYBILL(3, "运单号");
  private int code;
  private String msg;

  ComboardBarCodeTypeEnum(int code, String msg) {
    this.code = code;
    this.msg = msg;
  }

  public int getCode() {
    return code;
  }

  public void setCode(int code) {
    this.code = code;
  }

  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }
}
