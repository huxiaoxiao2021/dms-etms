package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class QueryLoadingRateRequest implements Serializable {

  private static final long serialVersionUID = 1398754086128269959L;
  /**
   * 车牌号
   */
  private String vehicleNumber;

  /**
   * 派车单明细号
   */
  private String transWorkItemCode;

  public String getTransWorkItemCode() {
    return transWorkItemCode;
  }

  public void setTransWorkItemCode(String transWorkItemCode) {
    this.transWorkItemCode = transWorkItemCode;
  }

  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }
}
