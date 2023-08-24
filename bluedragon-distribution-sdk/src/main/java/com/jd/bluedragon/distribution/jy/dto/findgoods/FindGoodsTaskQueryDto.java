package com.jd.bluedragon.distribution.jy.dto.findgoods;


public class FindGoodsTaskQueryDto {

  private Integer siteCode;
  private String workGridKey;
  private String date;
  private String waveStartTime;
  private String waveEndTime;

  public Integer getSiteCode() {
    return siteCode;
  }

  public void setSiteCode(Integer siteCode) {
    this.siteCode = siteCode;
  }

  public String getWorkGridKey() {
    return workGridKey;
  }

  public void setWorkGridKey(String workGridKey) {
    this.workGridKey = workGridKey;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
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
}
