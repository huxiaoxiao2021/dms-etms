package com.jd.bluedragon.distribution.board.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class CheckBoardStatusDto implements Serializable {
    private static final long serialVersionUID = 2843585002716222627L;
    /**
     *操作分拣中心id
     */
    private Integer siteCode;
    /**
     * 板列表
     */
    private List<String> boardCodes;

    /**
     *操作时间
     */
    private Date operateTime;

    private Integer receiveSiteCode;

    private Integer userCode;

    private String userName;

    private String barcode;


    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public List<String> getBoardCodes() {
        return boardCodes;
    }

    public void setBoardCodes(List<String> boardCodes) {
        this.boardCodes = boardCodes;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public Integer getUserCode() {
        return userCode;
    }

    public void setUserCode(Integer userCode) {
        this.userCode = userCode;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }
}
