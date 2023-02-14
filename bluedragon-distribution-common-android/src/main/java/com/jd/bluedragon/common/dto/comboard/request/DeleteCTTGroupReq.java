package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class DeleteCTTGroupReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = -6597303789414254407L;
  /**
   * 混扫任务编号
   */
  private String templateCode;

  public String getTemplateCode() {
    return templateCode;
  }

  public void setTemplateCode(String templateCode) {
    this.templateCode = templateCode;
  }
}
