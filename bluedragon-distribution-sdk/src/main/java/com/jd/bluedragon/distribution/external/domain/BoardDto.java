package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.Date;

public class BoardDto implements Serializable {

  private static final long serialVersionUID = -6617510874366811199L;

  private String boardCode;
  private String sendCode;
  private Integer scanCount;
  private Date createTime;
  private Date endTime;
  private Integer startSiteId;
  private Integer endSiteId;
  private String createUserErp;
  private String createUserName;
  private Integer comboardSource;
  private Integer comboardSiteId;

  public String getBoardCode() {
    return boardCode;
  }

  public void setBoardCode(String boardCode) {
    this.boardCode = boardCode;
  }

  public String getSendCode() {
    return sendCode;
  }

  public void setSendCode(String sendCode) {
    this.sendCode = sendCode;
  }

  public Integer getScanCount() {
    return scanCount;
  }

  public void setScanCount(Integer scanCount) {
    this.scanCount = scanCount;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getEndTime() {
    return endTime;
  }

  public void setEndTime(Date endTime) {
    this.endTime = endTime;
  }

  public Integer getStartSiteId() {
    return startSiteId;
  }

  public void setStartSiteId(Integer startSiteId) {
    this.startSiteId = startSiteId;
  }

  public Integer getEndSiteId() {
    return endSiteId;
  }

  public void setEndSiteId(Integer endSiteId) {
    this.endSiteId = endSiteId;
  }

  public String getCreateUserErp() {
    return createUserErp;
  }

  public void setCreateUserErp(String createUserErp) {
    this.createUserErp = createUserErp;
  }

  public String getCreateUserName() {
    return createUserName;
  }

  public void setCreateUserName(String createUserName) {
    this.createUserName = createUserName;
  }

  public Integer getComboardSource() {
    return comboardSource;
  }

  public void setComboardSource(Integer comboardSource) {
    this.comboardSource = comboardSource;
  }

  public Integer getComboardSiteId() {
    return comboardSiteId;
  }

  public void setComboardSiteId(Integer comboardSiteId) {
    this.comboardSiteId = comboardSiteId;
  }
}
