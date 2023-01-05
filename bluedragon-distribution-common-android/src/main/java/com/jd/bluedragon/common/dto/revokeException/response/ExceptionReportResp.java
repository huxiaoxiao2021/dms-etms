package com.jd.bluedragon.common.dto.revokeException.response;

import java.util.Date;

/**
 * @author liwenji
 * @description 异常提报查询
 * @date 2023-01-04 14:25
 */
public class ExceptionReportResp {

    /**
     * 异常编号
     */
    private String transAbnormalCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 封签异常提报时间
     */
    private Date reportTime;

    /**
     * 撤销异常提报时间
     */
    private Long revokeSurplusTime;

    /**
     * 线路类型
     */
    private String lineTypeName;

    /**
     * 异常类型
     */
    private String abnormalTypeName;

    public String getTransAbnormalCode() {
        return transAbnormalCode;
    }

    public void setTransAbnormalCode(String transAbnormalCode) {
        this.transAbnormalCode = transAbnormalCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Date getReportTime() {
        return reportTime;
    }

    public void setReportTime(Date reportTime) {
        this.reportTime = reportTime;
    }

    public String getAbnormalTypeName() {
        return abnormalTypeName;
    }

    public void setAbnormalTypeName(String abnormalTypeName) {
        this.abnormalTypeName = abnormalTypeName;
    }

    public Long getRevokeSurplusTime() {
        return revokeSurplusTime;
    }

    public void setRevokeSurplusTime(Long revokeSurplusTime) {
        this.revokeSurplusTime = revokeSurplusTime;
    }

    public String getLineTypeName() {
        return lineTypeName;
    }

    public void setLineTypeName(String lineTypeName) {
        this.lineTypeName = lineTypeName;
    }
}
