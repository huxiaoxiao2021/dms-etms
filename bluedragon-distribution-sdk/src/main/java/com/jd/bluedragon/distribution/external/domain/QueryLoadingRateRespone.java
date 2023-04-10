package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class QueryLoadingRateRespone implements Serializable {

  private static final long serialVersionUID = -3586117424559545914L;
  /**
   * 装载率
   */
  private String loadingRate;

  public String getLoadingRate() {
    return loadingRate;
  }

  public void setLoadingRate(String loadingRate) {
    this.loadingRate = loadingRate;
  }
}
