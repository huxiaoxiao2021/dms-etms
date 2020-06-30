package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.Date;

/**
 * 组板公共请求
 *
 * @author: hujiping
 * @date: 2020/6/29 12:24
 */
public class BoardCommonRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 板号
     * */
    private String boardCode;
    /**
     * 单号
     * */
    private String barCode;
    /**
     * 目的编码
     * */
    private Integer receiveSiteCode;
    /**
     * 目的名称
     * */
    private String receiveSiteName;
    /**
     * 操作人编码
     * */
    private Integer operateUserCode;
    /**
     * 操作人ERP
     * */
    private String operateUserErp;
    /**
     * 操作人名称
     * */
    private String operateUserName;
    /**
     * 操作人所属站点
     * */
    private Integer operateSiteCode;
    /**
     * 操作人所属站点名称
     * */
    private String operateSiteName;
    /**
     * 操作时间
     * */
    private Date operateTime;
    /**
     * 操作类型
     *  0：组板 1：取消组板
     * */
    private Integer businessType;
    /**
     * 是否强制组板
     * **/
    private boolean isForceCombination = false;

    public String getBoardCode() {
        return boardCode;
    }

    public void setBoardCode(String boardCode) {
        this.boardCode = boardCode;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
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

    public Integer getOperateUserCode() {
        return operateUserCode;
    }

    public void setOperateUserCode(Integer operateUserCode) {
        this.operateUserCode = operateUserCode;
    }

    public String getOperateUserErp() {
        return operateUserErp;
    }

    public void setOperateUserErp(String operateUserErp) {
        this.operateUserErp = operateUserErp;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getBusinessType() {
        return businessType;
    }

    public void setBusinessType(Integer businessType) {
        this.businessType = businessType;
    }

    public boolean getIsForceCombination() {
        return isForceCombination;
    }

    public void setIsForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }
}
