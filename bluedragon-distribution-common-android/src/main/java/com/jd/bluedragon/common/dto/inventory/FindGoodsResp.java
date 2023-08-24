package com.jd.bluedragon.common.dto.inventory;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;

public class FindGoodsResp extends BaseReq implements Serializable {
  /**
   * 扫描单号
   */
  private String barCode;
  /**
   * 数量
   */
  private Integer count;

  public String getBarCode() {
    return barCode;
  }

  public void setBarCode(String barCode) {
    this.barCode = barCode;
  }

  public Integer getCount() {
    return count;
  }

  public void setCount(Integer count) {
    this.count = count;
  }
}
