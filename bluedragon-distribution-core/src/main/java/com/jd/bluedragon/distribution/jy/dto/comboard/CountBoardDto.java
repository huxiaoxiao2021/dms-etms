package com.jd.bluedragon.distribution.jy.dto.comboard;

import java.util.List;

public class CountBoardDto {
  private Long startSiteId;

  private List<Long> endSiteIdList;

  private List<Integer> statusList;

  public Long getStartSiteId() {
    return startSiteId;
  }

  public void setStartSiteId(Long startSiteId) {
    this.startSiteId = startSiteId;
  }

  public List<Long> getEndSiteIdList() {
    return endSiteIdList;
  }

  public void setEndSiteIdList(List<Long> endSiteIdList) {
    this.endSiteIdList = endSiteIdList;
  }

  public List<Integer> getStatusList() {
    return statusList;
  }

  public void setStatusList(List<Integer> statusList) {
    this.statusList = statusList;
  }
}
