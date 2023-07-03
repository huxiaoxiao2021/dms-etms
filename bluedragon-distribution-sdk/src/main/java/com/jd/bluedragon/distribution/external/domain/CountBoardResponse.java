package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.List;

public class CountBoardResponse implements Serializable {

  private static final long serialVersionUID = -1918280575983566600L;
  List<ComboardFlowDto> comboardFlowDtoList;

  public List<ComboardFlowDto> getComboardFlowDtoList() {
    return comboardFlowDtoList;
  }

  public void setComboardFlowDtoList(
      List<ComboardFlowDto> comboardFlowDtoList) {
    this.comboardFlowDtoList = comboardFlowDtoList;
  }
}
