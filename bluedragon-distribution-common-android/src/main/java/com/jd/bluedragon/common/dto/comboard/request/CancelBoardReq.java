package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;
import java.util.List;

public class CancelBoardReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = -5371386941373048606L;
  private List<ComboardDetailDto>  cancelList;

  private String boardCode;

  /**
   * 大宗标识
   */
  private boolean bulkFlag;

  /**
   * 目的地站点名称
   */
  private String endSiteName;

  /**
   * 全选标识
   */
  private boolean selectAll;
  
  public List<ComboardDetailDto> getCancelList() {
    return cancelList;
  }

  public void setCancelList(List<ComboardDetailDto> cancelList) {
    this.cancelList = cancelList;
  }

  public String getBoardCode() {
    return boardCode;
  }

  public void setBoardCode(String boardCode) {
    this.boardCode = boardCode;
  }

  public boolean isBulkFlag() {
    return bulkFlag;
  }

  public void setBulkFlag(boolean bulkFlag) {
    this.bulkFlag = bulkFlag;
  }

  public String getEndSiteName() {
    return endSiteName;
  }

  public void setEndSiteName(String endSiteName) {
    this.endSiteName = endSiteName;
  }

  public boolean isSelectAll() {
    return selectAll;
  }

  public void setSelectAll(boolean selectAll) {
    this.selectAll = selectAll;
  }
}
