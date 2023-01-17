package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class QueryBelongBoardRequest implements Serializable {

  private static final long serialVersionUID = 6232712617548939585L;
  private String barCode;

  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }
}
