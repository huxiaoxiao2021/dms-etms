package com.jd.bluedragon.distribution.jy.enums;

public enum TransferTypeEnum {
  TRANSFER_OUT(1,"迁出"),
  TRANSFER_IN(2,"迁入");
  private Integer code;
  private String desc;

  TransferTypeEnum(Integer code, String desc) {
    this.code = code;
    this.desc = desc;
  }

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
