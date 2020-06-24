package com.jd.bluedragon.distribution.loadAndUnload;

import java.io.Serializable;
import java.util.Date;

/**
 * 卸车扫描请求对象
 *
 * @author: hujiping
 * @date: 2020/6/23 15:25
 */
public class UnloadCarScanRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 封车编码
     * */
    private String sealCarCode;
    /**
     * 板号
     * */
    private String boardCode;
    /**
     * 运单号/包裹号
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
    private String operateSiteCode;
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

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

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

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
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

    public boolean isForceCombination() {
        return isForceCombination;
    }

    public void setForceCombination(boolean forceCombination) {
        isForceCombination = forceCombination;
    }
}
