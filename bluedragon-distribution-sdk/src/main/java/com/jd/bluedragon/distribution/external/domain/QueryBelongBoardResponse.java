package com.jd.bluedragon.distribution.external.domain;

import java.io.Serializable;

public class QueryBelongBoardResponse implements Serializable {

  private static final long serialVersionUID = -8538443136954272123L;

  private BoardDto boardDto;

  public BoardDto getBoardDto() {
    return boardDto;
  }

  public void setBoardDto(BoardDto boardDto) {
    this.boardDto = boardDto;
  }
}
