package com.jd.bluedragon.distribution.jy.dto.tms;

import java.io.Serializable;

/**
 * 自建发货任务触发催派消息
 *
 * @author fanggang7
 * @copyright jd.com 京东物流JDL
 * @time 2023-09-14 18:32:15 周四
 */
public class TmsUrgeVehicleMq implements Serializable {

    private static final long serialVersionUID = -7013704681997529860L;

    private String userCode;

    private Long userId;

    private String userName;

    /**
     *操作单位编号
     */
    private Integer siteCode;

    /**
     *操作单位名称
     */
    private String siteName;

    /**
     * 自建任务bizId
     */
    private String bizId;

    /**
     * 任务编码
     */
    private String transJobCode;

    /**
     * 运力资源编码
     */
    private String transportCode;

    private Long operateTime;

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getTransJobCode() {
        return transJobCode;
    }

    public void setTransJobCode(String transJobCode) {
        this.transJobCode = transJobCode;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }

    public Long getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Long operateTime) {
        this.operateTime = operateTime;
    }
}
