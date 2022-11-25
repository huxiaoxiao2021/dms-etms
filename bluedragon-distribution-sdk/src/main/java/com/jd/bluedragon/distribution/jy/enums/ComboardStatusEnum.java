package com.jd.bluedragon.distribution.jy.enums;

public enum ComboardStatusEnum {
  PROCESSING(1, "进行中"),
  FINISHED(2, "结束"),
  SEALED(3, "封车"),
  CANCEL_SEAL(4, "取消封车");
  private int code;
  private String msg;

  ComboardStatusEnum(int code, String msg) {
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
