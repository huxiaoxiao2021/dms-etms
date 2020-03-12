package com.jd.bluedragon.distribution.financialForKA.domain;

import com.jd.ql.dms.common.web.mvc.api.BasePagerCondition;

/**
 * KA单号查询条件
 *
 * @author: hujiping
 * @date: 2020/2/26 21:59
 */
public class KaCodeCheckCondition extends BasePagerCondition {

    private static final long serialVersionUID = 1L;

    /**
     * 运单号
     * */
    private String waybillCode;
    /**
     * 操作机构ID
     * */
    private String operateSiteCode;
    /**
     * 操作人ERP
     * */
    private String operateErp;
    /**
     * 商家编码
     * */
    private String busiCode;
    /**
     * 校验结果
     * */
    private Integer checkResult;

    /**
     * 操作时间
     * */
    private String startTime;

    /**
     * 操作时间
     * */
    private String endTime;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(String operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public String getBusiCode() {
        return busiCode;
    }

    public void setBusiCode(String busiCode) {
        this.busiCode = busiCode;
    }

    public Integer getCheckResult() {
        return checkResult;
    }

    public void setCheckResult(Integer checkResult) {
        this.checkResult = checkResult;
    }

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
}
