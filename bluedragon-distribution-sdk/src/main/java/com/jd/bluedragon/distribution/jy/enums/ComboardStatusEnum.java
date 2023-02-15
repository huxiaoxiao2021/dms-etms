package com.jd.bluedragon.distribution.jy.enums;

public enum ComboardStatusEnum {
  PROCESSING(1, "进行中","组板中"),
  FINISHED(2, "结束","待封车"),
  SEALED(3, "封车","已封车"),
  CANCEL_SEAL(4, "取消封车","待封车");
  private int code;
  private String msg;
  private String desc;

  ComboardStatusEnum(int code, String msg, String desc) {
    this.code = code;
    this.msg = msg;
    this.desc = desc;
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

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }

  public static String getStatusDesc(int code){
    for(ComboardStatusEnum comboardStatusEnum : ComboardStatusEnum.values()){
      if(comboardStatusEnum.getCode() == code){
        return comboardStatusEnum.getDesc();
      }
    }
    return null;
  }
}
