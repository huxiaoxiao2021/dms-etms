package com.jd.bluedragon.common.dto.comboard.request;

import com.jd.bluedragon.common.dto.base.request.BaseReq;
import java.io.Serializable;
import java.util.List;

public class CancelBoardReq extends BaseReq implements Serializable {

  private static final long serialVersionUID = -5371386941373048606L;
  private List<ComboardDetailDto>  cancelList;

  public List<ComboardDetailDto> getCancelList() {
    return cancelList;
  }

  public void setCancelList(List<ComboardDetailDto> cancelList) {
    this.cancelList = cancelList;
  }
}
