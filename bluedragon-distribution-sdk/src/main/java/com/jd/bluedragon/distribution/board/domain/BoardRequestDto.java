package com.jd.bluedragon.distribution.board.domain;


import java.io.Serializable;

/**
 * BoardCheckDto
 * 板号校验返回信息
 *
 * @author jiaowenqiang
 * @date 2019/7/8
 */
public class BoardRequestDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 接收站点编号
     */
    private Integer receiveSiteCode;

    /**
     * 接收站点名称
     */
    private String receiveSiteName;

    /**
     * 板号
     */
    private String boardCode;

    /**
     * 错组标识: 1是错组，0是非错组
     */
    private Integer flowDisaccord;

    @Override
    public String toString() {
        return "BoardCheckDto{" +
                "receiveSiteCode=" + receiveSiteCode +
                ", receiveSiteName='" + receiveSiteName + '\'' +
                ", boardCode='" + boardCode + '\'' +
                '}';
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public Integer getFlowDisaccord() {
        return flowDisaccord;
    }

    public void setFlowDisaccord(Integer flowDisaccord) {
        this.flowDisaccord = flowDisaccord;
    }
}
