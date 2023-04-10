package com.jd.bluedragon.common.dto.send.response;

import java.io.Serializable;

public class GetTaskSimpleCodeResp implements Serializable {


  private static final long serialVersionUID = -6293002136768047552L;
  /**
   * 任务简码
   */
  private String taskSimpleCode;

  public String getTaskSimpleCode() {
    return taskSimpleCode;
  }

  public void setTaskSimpleCode(String taskSimpleCode) {
    this.taskSimpleCode = taskSimpleCode;
  }
}
