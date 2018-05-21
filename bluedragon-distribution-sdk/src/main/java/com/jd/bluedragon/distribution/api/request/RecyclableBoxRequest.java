package com.jd.bluedragon.distribution.api.request;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class RecyclableBoxRequest {

  private List<String> uniqueCode;

  private String oneLevelNodeNo;

  private String twoLevelNodeNo;

  private String threeLevelNodeNo;

  private String nodeName;

  private Integer nodeType;

  private String orderNo;

  /**
   * 加备注
   */
  private String wayBillNo;

  private String operator;

  private String operateTime;

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
}
