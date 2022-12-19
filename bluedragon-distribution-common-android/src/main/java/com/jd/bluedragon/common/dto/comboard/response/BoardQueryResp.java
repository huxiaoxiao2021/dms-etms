package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class BoardQueryResp implements Serializable {

  private static final long serialVersionUID = 3620530938777882756L;

  List<BoardDto> boardDtoList;

  public List<BoardDto> getBoardDtoList() {
    return boardDtoList;
  }

  public void setBoardDtoList(List<BoardDto> boardDtoList) {
    this.boardDtoList = boardDtoList;
  }
}
