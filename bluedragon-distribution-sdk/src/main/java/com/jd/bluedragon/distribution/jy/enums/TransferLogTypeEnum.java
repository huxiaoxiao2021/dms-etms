package com.jd.bluedragon.distribution.jy.enums;

public enum TransferLogTypeEnum {
  SAME_WAY_BIND(1,"同流向迁移（自建任务绑定）"),
  SAME_WAY_TRANSFER(2,"同流向迁移（迁移功能）"),
  NOT_SAME_WAY_TRANSFER_OLD_BATCH(3,"不同流向迁移-原批次(解除)"),
  NOT_SAME_WAY_TRANSFER_NEW_BATCH(4,"不同流向迁移-新批次(创建)");
  private Integer code;
  private String desc;

  TransferLogTypeEnum(Integer code, String desc) {
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
