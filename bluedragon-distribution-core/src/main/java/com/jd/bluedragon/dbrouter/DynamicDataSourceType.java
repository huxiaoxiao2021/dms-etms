package com.jd.bluedragon.dbrouter;

public enum DynamicDataSourceType {
  DEFAULT("default", "默认数据源"),
  JY_CORE("jyCore", "jy核心数据源"),
  AGGS_MAIN("aggsMain", "aggs聚合单拆主库"),
  AGGS_SLAVE("aggsSlave", "aggs聚合单拆从库");

  private String name;
  private String desc;

  DynamicDataSourceType(String name, String desc) {
    this.name = name;
    this.desc = desc;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDesc() {
    return desc;
  }

  public void setDesc(String desc) {
    this.desc = desc;
  }
}
