package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.util.Date;

public class WaitingVehicleDistribution extends BaseSendVehicle{
    private static final long serialVersionUID = -4874713646811057276L;

    /**
     * 待派车任务号
     */
    private String transJobCode;

    /**
     * 运力资源编码
     */
    private String transportCode;

    /**
     * 预计派车时间
     */
    private Date planDispatchVehicleTime;

    /**
     * 目的场地编码
     */
    private Integer destSiteCode;

    /**
     * 目的场地名称
     */
    private String destSiteName;

    public String getTransJobCode() {
        return transJobCode;
    }

    public void setTransJobCode(String transJobCode) {
        this.transJobCode = transJobCode;
    }

    public Date getPlanDispatchVehicleTime() {
        return planDispatchVehicleTime;
    }

    public void setPlanDispatchVehicleTime(Date planDispatchVehicleTime) {
        this.planDispatchVehicleTime = planDispatchVehicleTime;
    }

    public Integer getDestSiteCode() {
        return destSiteCode;
    }

    public void setDestSiteCode(Integer destSiteCode) {
        this.destSiteCode = destSiteCode;
    }

    public String getDestSiteName() {
        return destSiteName;
    }

    public void setDestSiteName(String destSiteName) {
        this.destSiteName = destSiteName;
    }

    public String getTransportCode() {
        return transportCode;
    }

    public void setTransportCode(String transportCode) {
        this.transportCode = transportCode;
    }
}
