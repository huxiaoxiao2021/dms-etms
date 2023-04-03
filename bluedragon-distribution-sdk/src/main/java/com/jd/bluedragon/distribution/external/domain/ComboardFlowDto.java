package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class ComboardFlowDto implements Serializable {

  private static final long serialVersionUID = -8578261331593728197L;

  /**
   * 始发站点
   */
  private Integer startSiteId;
  /**
   * 目的站点
   */
  private Integer endSiteId;
  /**
   * 该流向组板数量
   */
  private Integer boardCount;
  public Integer getStartSiteId() {
    return startSiteId;
  }

  public void setStartSiteId(Integer startSiteId) {
    this.startSiteId = startSiteId;
  }

  public Integer getEndSiteId() {
    return endSiteId;
  }

  public void setEndSiteId(Integer endSiteId) {
    this.endSiteId = endSiteId;
  }

  public Integer getBoardCount() {
    return boardCount;
  }

  public void setBoardCount(Integer boardCount) {
    this.boardCount = boardCount;
  }
}
