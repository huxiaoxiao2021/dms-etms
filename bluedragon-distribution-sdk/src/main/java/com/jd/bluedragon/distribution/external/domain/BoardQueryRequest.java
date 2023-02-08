package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class BoardQueryRequest implements Serializable {

  private static final long serialVersionUID = -4289024967192450699L;
  private Integer startSiteId;
  private Integer endSiteId;
  private Integer pageNo;
  private Integer pageSize;

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

  public Integer getPageNo() {
    return pageNo;
  }

  public void setPageNo(Integer pageNo) {
    this.pageNo = pageNo;
  }

  public Integer getPageSize() {
    return pageSize;
  }

  public void setPageSize(Integer pageSize) {
    this.pageSize = pageSize;
  }
}
