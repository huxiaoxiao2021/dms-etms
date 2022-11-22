package com.jd.bluedragon.common.dto.comboard.response;

import com.jd.bluedragon.common.dto.comboard.request.ComboardDetailDto;
import java.io.Serializable;
import java.util.List;

public class ComboardDetailResp implements Serializable {

  private static final long serialVersionUID = -4705929805932780236L;
  /**
   * 大宗标识
   */
  private boolean bulkFlag;
  /**
   * 箱数量
   */
  private Integer boxCount;
  /**
   * 包裹数量
   */
  private Integer packageCount;
  /**
   * 板内明细
   */
  List<ComboardDetailDto> comboardDetailDtoList;

  public boolean isBulkFlag() {
    return bulkFlag;
  }

  public void setBulkFlag(boolean bulkFlag) {
    this.bulkFlag = bulkFlag;
  }

  public Integer getBoxCount() {
    return boxCount;
  }

  public void setBoxCount(Integer boxCount) {
    this.boxCount = boxCount;
  }

  public Integer getPackageCount() {
    return packageCount;
  }

  public void setPackageCount(Integer packageCount) {
    this.packageCount = packageCount;
  }

  public List<ComboardDetailDto> getComboardDetailDtoList() {
    return comboardDetailDtoList;
  }

  public void setComboardDetailDtoList(
      List<ComboardDetailDto> comboardDetailDtoList) {
    this.comboardDetailDtoList = comboardDetailDtoList;
  }
}
