package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;
import java.util.List;

public class BoardQueryResp implements Serializable {

  private static final long serialVersionUID = 3620530938777882756L;

  List<BoardDto> boardDtoList;
  
  private Long boardTotal;

  /**
   * 一次性提交封车的上限
   */
  private Integer boardLimit;
  
  public List<BoardDto> getBoardDtoList() {
    return boardDtoList;
  }

  public void setBoardDtoList(List<BoardDto> boardDtoList) {
    this.boardDtoList = boardDtoList;
  }

  public Long getBoardTotal() {
    return boardTotal;
  }

  public void setBoardTotal(Long boardTotal) {
    this.boardTotal = boardTotal;
  }

  public Integer getBoardLimit() {
    return boardLimit;
  }

  public void setBoardLimit(Integer boardLimit) {
    this.boardLimit = boardLimit;
  }
}
