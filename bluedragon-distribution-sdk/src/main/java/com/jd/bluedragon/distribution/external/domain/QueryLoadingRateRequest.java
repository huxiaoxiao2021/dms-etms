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
  private String transWorkCode;

  private Integer operateSiteId;

  public String getVehicleNumber() {
    return vehicleNumber;
  }

  public void setVehicleNumber(String vehicleNumber) {
    this.vehicleNumber = vehicleNumber;
  }

  public String getTransWorkCode() {
    return transWorkCode;
  }

  public void setTransWorkCode(String transWorkCode) {
    this.transWorkCode = transWorkCode;
  }

  public Integer getOperateSiteId() {
    return operateSiteId;
  }

  public void setOperateSiteId(Integer operateSiteId) {
    this.operateSiteId = operateSiteId;
  }
}
