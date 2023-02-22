package com.jd.bluedragon.common.dto.comboard.response;

import java.io.Serializable;

public class QueryBelongBoardResp implements Serializable {

  /**
   * 板号
   */
  private String boardCode;
  /**
   * 目的站点id
   */
  private Integer endSiteId;

  /**
   * 板详情
   */
  private BoardDto boardDto;

  public String getBoardCode() {
    return boardCode;
  }

  public void setBoardCode(String boardCode) {
    this.boardCode = boardCode;
  }

  public Integer getEndSiteId() {
    return endSiteId;
  }

  public void setEndSiteId(Integer endSiteId) {
    this.endSiteId = endSiteId;
  }

  public BoardDto getBoardDto() {
    return boardDto;
  }

  public void setBoardDto(BoardDto boardDto) {
    this.boardDto = boardDto;
  }
}
