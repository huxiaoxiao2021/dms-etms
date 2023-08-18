package com.jd.bluedragon.distribution.jy.dto.findgoods;

import java.io.Serializable;

public class WaitFindPackageQueryDto implements Serializable {

  private static final long serialVersionUID = -3917466335624033589L;
  private Integer siteCode;
  private String waveTime;
  private Integer pageNo;
  private Integer pageSize;

  public Integer getSiteCode() {
    return siteCode;
  }

  public void setSiteCode(Integer siteCode) {
    this.siteCode = siteCode;
  }

  public String getWaveTime() {
    return waveTime;
  }

  public void setWaveTime(String waveTime) {
    this.waveTime = waveTime;
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
