package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class QueryBelongBoardResp implements Serializable {

  /**
   * 板号
   */
  private String boardCode;
  /**
   * 目的站点id
   */
  private Integer endSiteId;

  public String getBoardCode() {
    return boardCode;
  }

  public void setBoardCode(String boardCode) {
    this.boardCode = boardCode;
  }

  public Integer getEndSiteId() {
    return endSiteId;
  }

  public void setEndSiteId(Integer endSiteId) {
    this.endSiteId = endSiteId;
  }
}
