package com.jd.bluedragon.common.dto.operation.workbench.send.response;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName BaseSendVehicleDetail
 * @Description
 * @Author wyh
 * @Date 2022/5/18 17:36
 **/
public class BaseSendVehicleDetail implements Serializable {

    private static final long serialVersionUID = 3532519446616605954L;

    /**
     * 预计发货时间
     */
    private Date planDepartTime;

    /**
     * 目的场地
     */
    private Long endSiteId;

    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 发货明细流向ID
     */
    private String sendDetailBizId;

    public Date getPlanDepartTime() {
        return planDepartTime;
    }

    public void setPlanDepartTime(Date planDepartTime) {
        this.planDepartTime = planDepartTime;
    }

    public Long getEndSiteId() {
        return endSiteId;
    }

    public void setEndSiteId(Long endSiteId) {
        this.endSiteId = endSiteId;
    }

    public String getEndSiteName() {
        return endSiteName;
    }

    public void setEndSiteName(String endSiteName) {
        this.endSiteName = endSiteName;
    }

    public String getSendDetailBizId() {
        return sendDetailBizId;
    }

    public void setSendDetailBizId(String sendDetailBizId) {
        this.sendDetailBizId = sendDetailBizId;
    }
}
