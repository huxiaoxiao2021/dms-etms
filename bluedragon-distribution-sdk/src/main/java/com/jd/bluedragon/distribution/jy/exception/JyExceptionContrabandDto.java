package com.jd.bluedragon.distribution.jy.exception;

import java.io.Serializable;

/**
 * 异常-违禁品上报dto
 */
public class JyExceptionContrabandDto implements Serializable {

    private static final long serialVersionUID = -1446398935944895849L;

    /**
     * 业务id
     */
    private String bizId;
    /**
     * 站点code
     */
    private Integer siteCode;
    /**
     * 站点name
     */

    private String siteName;

    /**
     * 异常条码
     */
    private String barCode;

    /**
     * 违禁品类型（1：扣减 2.航空转陆运 3.退回）
     */
    private Integer contrabandType;
    /**
     * 客服反馈类型（1：更换包装下传 2：再派、未联系上、下传 3：破损已理赔、退回 4：报废 5：补单、补差）
     */
    private Integer feedBackType;

    /**
     * 货物情况
     */
    private String description;


    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public Integer getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Integer siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public Integer getContrabandType() {
        return contrabandType;
    }

    public void setContrabandType(Integer contrabandType) {
        this.contrabandType = contrabandType;
    }

    public Integer getFeedBackType() {
        return feedBackType;
    }

    public void setFeedBackType(Integer feedBackType) {
        this.feedBackType = feedBackType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


}