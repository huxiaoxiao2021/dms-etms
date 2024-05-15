package com.jd.bluedragon.distribution.jy.dto.findgoods;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;
import java.io.Serializable;

/**
 * 清场任务创建dto
 * @author weixiaofeng
 * @date 2024-04-02
 */
public class CreateFindGoodsTask extends JyReqBaseDto implements Serializable {

  private static final long serialVersionUID = 6719232759149459943L;
  private Integer siteCode;
  private String workGridKey;
  private String date;
  private String waveStartTime;
  private String waveEndTime;

  /**
   * 网格编码
   */
  private String gridCode;
  /**
   * 网格名称
   */
  private String gridName;

  public String getGridCode() {
    return gridCode;
  }

  public void setGridCode(String gridCode) {
    this.gridCode = gridCode;
  }

  public String getGridName() {
    return gridName;
  }

  public void setGridName(String gridName) {
    this.gridName = gridName;
  }

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
