package com.jd.bluedragon.common.dto.send.request;

import java.io.Serializable;
import java.util.Date;

public class OperateProgressRequest implements Serializable {

  private static final long serialVersionUID = -5532656559889510864L;

  /**
   * 操作维度  装车：派车单号/卸车：封车编码
   */
  private String operationCode;
  /**
   * 场地
   */
  private Integer operateSiteId;
  /**
   * 操作进度
   */
  private String operationProgress;
  /**
   * 操作类型 1 装车 2卸车
   */
  private Integer operationType;
  /**
   * 时间
   */
  private Date operationTime;

  /**
   * 扫描第一枪操作时间
   */
  private Date firstScanTime;

  public String getOperationCode() {
    return operationCode;
  }

  public void setOperationCode(String operationCode) {
    this.operationCode = operationCode;
  }

  public Integer getOperateSiteId() {
    return operateSiteId;
  }

  public void setOperateSiteId(Integer operateSiteId) {
    this.operateSiteId = operateSiteId;
  }

  public String getOperationProgress() {
    return operationProgress;
  }

  public void setOperationProgress(String operationProgress) {
    this.operationProgress = operationProgress;
  }

  public Integer getOperationType() {
    return operationType;
  }

  public void setOperationType(Integer operationType) {
    this.operationType = operationType;
  }

  public Date getOperationTime() {
    return operationTime;
  }

  public void setOperationTime(Date operationTime) {
    this.operationTime = operationTime;
  }

  public Date getFirstScanTime() {
    return firstScanTime;
  }

  public void setFirstScanTime(Date firstScanTime) {
    this.firstScanTime = firstScanTime;
  }
}
