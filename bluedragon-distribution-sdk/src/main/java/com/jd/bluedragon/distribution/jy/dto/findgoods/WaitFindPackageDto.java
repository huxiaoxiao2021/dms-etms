package com.jd.bluedragon.distribution.jy.dto.findgoods;

import java.io.Serializable;

public class WaitFindPackageDto implements Serializable {

  private static final long serialVersionUID = 61180305822171089L;

  private String packageCode;
  private Integer findType;
  private Integer findStatus;

  public Integer getFindStatus() {
    return findStatus;
  }

  public void setFindStatus(Integer findStatus) {
    this.findStatus = findStatus;
  }

  public String getPackageCode() {
    return packageCode;
  }

  public void setPackageCode(String packageCode) {
    this.packageCode = packageCode;
  }

  public Integer getFindType() {
    return findType;
  }

  public void setFindType(Integer findType) {
    this.findType = findType;
  }
}
