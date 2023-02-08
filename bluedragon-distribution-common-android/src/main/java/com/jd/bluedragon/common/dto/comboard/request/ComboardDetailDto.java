package com.jd.bluedragon.common.dto.comboard.request;

import java.io.Serializable;

public class ComboardDetailDto implements Serializable {

  private static final long serialVersionUID = 4793580953863263253L;
  private String barCode;
  /**
   * 类型 1箱号 2包裹号 3运单号
   */
  private Integer type;
  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public Integer getType() {
    return type;
  }

  public void setType(Integer type) {
    this.type = type;
  }
  
}
