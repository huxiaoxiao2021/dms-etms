package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class QueryBelongBoardReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = 6760429876025860405L;
  private String barCode;
  
  private Integer endSiteId;

  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public Integer getEndSiteId() {
    return endSiteId;
  }

  public void setEndSiteId(Integer endSiteId) {
    this.endSiteId = endSiteId;
  }
}
