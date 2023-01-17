package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;
import java.util.List;

public class BoardQueryResponse implements Serializable {

  private static final long serialVersionUID = 4623131786604054877L;

  List<BoardDto> boardDtoList;

  private Long total;

  public List<BoardDto> getBoardDtoList() {
    return boardDtoList;
  }

  public void setBoardDtoList(
      List<BoardDto> boardDtoList) {
    this.boardDtoList = boardDtoList;
  }

  public Long getTotal() {
    return total;
  }

  public void setTotal(Long total) {
    this.total = total;
  }
}
