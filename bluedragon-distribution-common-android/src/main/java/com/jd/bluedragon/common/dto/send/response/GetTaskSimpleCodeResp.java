package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class GetTaskSimpleCodeResp implements Serializable {


  private static final long serialVersionUID = -6293002136768047552L;
  /**
   * 任务简码
   */
  private String taskSimpleCode;
  /**
   * 线路编码
   */
  private String routeLineCode;

  /**
   * 线路名称
   */
  private String routeLineName;

  public String getRouteLineCode() {
    return routeLineCode;
  }

  public void setRouteLineCode(String routeLineCode) {
    this.routeLineCode = routeLineCode;
  }

  public String getRouteLineName() {
    return routeLineName;
  }

  public void setRouteLineName(String routeLineName) {
    this.routeLineName = routeLineName;
  }

  public String getTaskSimpleCode() {
    return taskSimpleCode;
  }

  public void setTaskSimpleCode(String taskSimpleCode) {
    this.taskSimpleCode = taskSimpleCode;
  }
}
