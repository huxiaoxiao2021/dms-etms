package com.jd.bluedragon.distribution.jy.dto.findgoods;

import java.io.Serializable;
import java.util.Date;

public class FindGoodsTaskDto implements Serializable {

  private static final long serialVersionUID = -5530076502570255296L;
  private Long id;

  private String bizId;

  private String workGridKey;

  private Long siteCode;

  private String waveStartTime;

  private String waveEndTime;

  private Integer taskStatus;

  private Integer waitFindCount;

  private Integer haveFindCount;

  private String photoStatus;

  private String createUserErp;

  private String createUserName;

  private String updateUserErp;

  private String updateUserName;

  private Date createTime;

  private Date updateTime;

  private Integer yn;

  private Date ts;

  private String taskDate;

  private boolean newCreateFlag;

  public boolean getNewCreateFlag() {
    return newCreateFlag;
  }

  public void setNewCreateFlag(boolean newCreateFlag) {
    this.newCreateFlag = newCreateFlag;
  }

  public String getTaskDate() {
    return taskDate;
  }

  public void setTaskDate(String taskDate) {
    this.taskDate = taskDate;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBizId() {
    return bizId;
  }

  public void setBizId(String bizId) {
    this.bizId = bizId;
  }

  public String getWorkGridKey() {
    return workGridKey;
  }

  public void setWorkGridKey(String workGridKey) {
    this.workGridKey = workGridKey;
  }

  public Long getSiteCode() {
    return siteCode;
  }

  public void setSiteCode(Long siteCode) {
    this.siteCode = siteCode;
  }

  public String getWaveStartTime() {
    return waveStartTime;
  }

  public void setWaveStartTime(String waveStartTime) {
    this.waveStartTime = waveStartTime;
  }

  public String getWaveEndTime() {
    return waveEndTime;
  }

  public void setWaveEndTime(String waveEndTime) {
    this.waveEndTime = waveEndTime;
  }

  public Integer getTaskStatus() {
    return taskStatus;
  }

  public void setTaskStatus(Integer taskStatus) {
    this.taskStatus = taskStatus;
  }

  public Integer getWaitFindCount() {
    return waitFindCount;
  }

  public void setWaitFindCount(Integer waitFindCount) {
    this.waitFindCount = waitFindCount;
  }

  public Integer getHaveFindCount() {
    return haveFindCount;
  }

  public void setHaveFindCount(Integer haveFindCount) {
    this.haveFindCount = haveFindCount;
  }

  public String getPhotoStatus() {
    return photoStatus;
  }

  public void setPhotoStatus(String photoStatus) {
    this.photoStatus = photoStatus;
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

  public String getUpdateUserErp() {
    return updateUserErp;
  }

  public void setUpdateUserErp(String updateUserErp) {
    this.updateUserErp = updateUserErp;
  }

  public String getUpdateUserName() {
    return updateUserName;
  }

  public void setUpdateUserName(String updateUserName) {
    this.updateUserName = updateUserName;
  }

  public Date getCreateTime() {
    return createTime;
  }

  public void setCreateTime(Date createTime) {
    this.createTime = createTime;
  }

  public Date getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(Date updateTime) {
    this.updateTime = updateTime;
  }

  public Integer getYn() {
    return yn;
  }

  public void setYn(Integer yn) {
    this.yn = yn;
  }

  public Date getTs() {
    return ts;
  }

  public void setTs(Date ts) {
    this.ts = ts;
  }
}
