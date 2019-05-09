package com.jd.bluedragon.distribution.print.request;

import com.jd.bluedragon.distribution.client.domain.ClientOperateRequest;

/**
 * <P>
 * </p>
 *
 * @author wuzuxiang
 * @since 2019/4/5
 */

public class WaybillRescheduleRequest extends ClientOperateRequest{

    /**
     * 始发分拣中心
     */
    private Integer startDmsCode;

    /**
     * 始发分拣中心名称
     */
    private String startDmsName;

    /**
     * 调度站点
     */
    private Integer rescheduleSiteCode;

    /**
     * 调度站点名称
     */
    private String rescheduleSiteName;

    /**
     * 是否无纸化
     */
    private Integer paperless;

    /**
     * 始发站点类型
     */
    private Integer startSiteType;

    public Integer getStartDmsCode() {
        return startDmsCode;
    }

    public void setStartDmsCode(Integer startDmsCode) {
        this.startDmsCode = startDmsCode;
    }

    public String getStartDmsName() {
        return startDmsName;
    }

    public void setStartDmsName(String startDmsName) {
        this.startDmsName = startDmsName;
    }

    public Integer getRescheduleSiteCode() {
        return rescheduleSiteCode;
    }

    public void setRescheduleSiteCode(Integer rescheduleSiteCode) {
        this.rescheduleSiteCode = rescheduleSiteCode;
    }

    public String getRescheduleSiteName() {
        return rescheduleSiteName;
    }

    public void setRescheduleSiteName(String rescheduleSiteName) {
        this.rescheduleSiteName = rescheduleSiteName;
    }

    public Integer getPaperless() {
        return paperless;
    }

    public void setPaperless(Integer paperless) {
        this.paperless = paperless;
    }

    public Integer getStartSiteType() {
        return startSiteType;
    }

    public void setStartSiteType(Integer startSiteType) {
        this.startSiteType = startSiteType;
    }

    @Override
    public String toString() {
        return "WaybillRescheduleRequest{" +
                "startDmsCode=" + startDmsCode +
                ", startDmsName='" + startDmsName + '\'' +
                ", rescheduleSiteCode=" + rescheduleSiteCode +
                ", rescheduleSiteName='" + rescheduleSiteName + '\'' +
                ", paperless=" + paperless +
                ", startSiteType=" + startSiteType +
                "} " + super.toString();
    }
}
