package com.jd.bluedragon.distribution.inspection;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

import java.util.Date;

/**
 * @author lijie
 * @date 2020/2/19 19:39
 */
public class InspectionCheckCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 起始时间
     * */
    private String startTime;

    /**
     * 截至时间
     * */
    private String endTime;

    /**
     * 是否集齐：1-集齐，2-未集齐，3-全部
     * */
    private Integer gatherType;

    /**
     * 运单号
     * */
    private String waybillCode;

    /**
     * 操作人erp
     * */
    private String operatorErp;

    /**
     * 操作人ID
     * */
    private Integer createUserCode;

    /**
     *操作人所属的分拣中心
     * */
    private Integer createSiteCode;

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getGatherType() {
        return gatherType;
    }

    public void setGatherType(Integer gatherType) {
        this.gatherType = gatherType;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public Integer getCreateUserCode() {
        return createUserCode;
    }

    public void setCreateUserCode(Integer createUserCode) {
        this.createUserCode = createUserCode;
    }

    public Integer getCreateSiteCode() {
        return createSiteCode;
    }

    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
}
