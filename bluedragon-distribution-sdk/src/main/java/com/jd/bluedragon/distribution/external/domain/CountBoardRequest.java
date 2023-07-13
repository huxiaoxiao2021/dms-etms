package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.List;

public class CountBoardRequest implements Serializable {

  private static final long serialVersionUID = -7783230545052068934L;
  private Integer startSiteId;
  private List<Integer> endSiteIdList;

  public Integer getStartSiteId() {
    return startSiteId;
  }

  public void setStartSiteId(Integer startSiteId) {
    this.startSiteId = startSiteId;
  }

  public List<Integer> getEndSiteIdList() {
    return endSiteIdList;
  }

  public void setEndSiteIdList(List<Integer> endSiteIdList) {
    this.endSiteIdList = endSiteIdList;
  }
}
