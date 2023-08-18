package com.jd.bluedragon.distribution.jy.dto.findgoods;

import com.jd.bluedragon.distribution.jy.dto.JyReqBaseDto;
import java.io.Serializable;
import java.util.List;

public class DistributPackageDto extends JyReqBaseDto implements Serializable {

  private static final long serialVersionUID = 1481362125482371612L;
  /**
   * 找货任务bizId
   */
  private String findGoodsTaskBizId;
  /**
   * 待找获取列表
   */
  List<WaitFindPackageDto> waitFindPackageDtoList;

  public String getFindGoodsTaskBizId() {
    return findGoodsTaskBizId;
  }

  public void setFindGoodsTaskBizId(String findGoodsTaskBizId) {
    this.findGoodsTaskBizId = findGoodsTaskBizId;
  }

  public List<WaitFindPackageDto> getWaitFindPackageDtoList() {
    return waitFindPackageDtoList;
  }

  public void setWaitFindPackageDtoList(
      List<WaitFindPackageDto> waitFindPackageDtoList) {
    this.waitFindPackageDtoList = waitFindPackageDtoList;
  }
}
