package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

public class RecyclableBoxRequest implements Serializable{
  private static final long serialVersionUID = 1L;

  /**
   * 青流箱号
   */
  private List<String> uniqueCode;

  private String oneLevelNodeNo;

  private String twoLevelNodeNo;

  private String threeLevelNodeNo;

  private String nodeName;

  private Integer nodeType;

  /**
   * 订单号
   */
  private String orderNo;

  /**
   * 运单号
   */
  private String wayBillNo;

  /**
   * 操作人erp
   */
  private String operator;

  /**
   * 操作时间
   */
  private String operateTime;

  /**
   * 流水号
   */
  private String batchCode;

  private String sourceSysCode;

  public List<String> getUniqueCode() {
    return uniqueCode;
  }

  public void setUniqueCode(List<String> uniqueCode) {
    this.uniqueCode = uniqueCode;
  }

  public String getOneLevelNodeNo() {
    return oneLevelNodeNo;
  }

  public void setOneLevelNodeNo(String oneLevelNodeNo) {
    this.oneLevelNodeNo = oneLevelNodeNo;
  }

  public String getTwoLevelNodeNo() {
    return twoLevelNodeNo;
  }

  public void setTwoLevelNodeNo(String twoLevelNodeNo) {
    this.twoLevelNodeNo = twoLevelNodeNo;
  }

  public String getThreeLevelNodeNo() {
    return threeLevelNodeNo;
  }

  public void setThreeLevelNodeNo(String threeLevelNodeNo) {
    this.threeLevelNodeNo = threeLevelNodeNo;
  }

  public String getNodeName() {
    return nodeName;
  }

  public void setNodeName(String nodeName) {
    this.nodeName = nodeName;
  }

  public Integer getNodeType() {
    return nodeType;
  }

  public void setNodeType(Integer nodeType) {
    this.nodeType = nodeType;
  }

  public String getOrderNo() {
    return orderNo;
  }

  public void setOrderNo(String orderNo) {
    this.orderNo = orderNo;
  }

  public String getWayBillNo() {
    return wayBillNo;
  }

  public void setWayBillNo(String wayBillNo) {
    this.wayBillNo = wayBillNo;
  }

  public String getOperator() {
    return operator;
  }

  public void setOperator(String operator) {
    this.operator = operator;
  }

  public String getOperateTime() { return  operateTime; }

  public void setOperateTime(String operateTime) {
    this.operateTime = operateTime;
  }

  public String getBatchCode() {
    return batchCode;
  }

  public void setBatchCode(String batchCode) {
    this.batchCode = batchCode;
  }

  public String getSourceSysCode() {
    return sourceSysCode;
  }

  public void setSourceSysCode(String sourceSysCode) {
    this.sourceSysCode = sourceSysCode;
  }
}
