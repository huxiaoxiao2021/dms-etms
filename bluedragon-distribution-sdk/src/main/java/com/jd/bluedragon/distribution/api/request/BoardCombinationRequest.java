package com.jd.bluedragon.distribution.api.request;


import com.jd.ql.dms.common.domain.JdRequest;

/**
 * Created by xumei3 on 2018/3/27.
 */
public class BoardCombinationRequest extends JdRequest {

    private static final long serialVersionUID = 1L;

    /** 板号 **/
    private String boardCode;

    /** 目的地编码 **/
    private Integer receiveSiteCode;

    /** 目的地名称 **/
    private String receiveSiteName;

    /** 箱号或包裹号 **/
    private String boxOrPackageCode;

    /** 是否强制组板 **/
    private boolean isForceCombination;


    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getReceiveSiteName() {
        return receiveSiteName;
    }

    public void setReceiveSiteName(String receiveSiteName) {
        this.receiveSiteName = receiveSiteName;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }

    public String getBoxOrPackageCode() {
        return boxOrPackageCode;
    }

    public void setBoxOrPackageCode(String boxOrPackageCode) {
        this.boxOrPackageCode = boxOrPackageCode;
    }

    public boolean isForceCombination() {
        return isForceCombination;
    }

    public void setForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }
}
