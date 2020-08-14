package com.jd.bluedragon.common.dto.blockcar.response;

import java.util.List;

public class PreSealVehicleMeasureDto {

  /** 运力编码 */
  private String transportCode;

  /** 车牌号List */
  private List<VehicleMeasureDto> vehicleMeasureInfoList;

  /** 运输类型 */
  private Integer transWay;

  /** 运输类型名称 */
  private String transWayName;

  public String getTransportCode() {
    return transportCode;
  }

  public void setTransportCode(String transportCode) {
    this.transportCode = transportCode;
  }

  public List<VehicleMeasureDto> getVehicleMeasureInfoList() {
    return vehicleMeasureInfoList;
  }

  public void setVehicleMeasureInfoList(List<VehicleMeasureDto> vehicleMeasureInfoList) {
    this.vehicleMeasureInfoList = vehicleMeasureInfoList;
  }

  public Integer getTransWay() {
    return transWay;
  }

  public void setTransWay(Integer transWay) {
    this.transWay = transWay;
  }

  public String getTransWayName() {
    return transWayName;
  }

  public void setTransWayName(String transWayName) {
    this.transWayName = transWayName;
  }
}
