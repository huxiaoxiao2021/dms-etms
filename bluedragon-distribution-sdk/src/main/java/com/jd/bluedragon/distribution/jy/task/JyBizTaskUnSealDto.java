package com.jd.bluedragon.distribution.jy.task;

import java.io.Serializable;
import java.util.Date;

/**
 * 天官赐福 ◎ 百无禁忌
 *
 * @Auther: 刘铎（liuduo8）
 * @Date: 2022/4/5
 * @Description:
 */
public class JyBizTaskUnSealDto implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 业务主键 = 封车编码
     */
    private String bizId;
    /**
     * 封车编码
     */
    private String sealCarCode;

    /**
     * 车牌号
     */
    private String vehicleNumber;

    /**
     * 始发场地ID
     */
    private Long startSiteId;
    /**
     * 始发场地名称
     */
    private String startSiteName;
    /**
     * 目的场地ID
     */
    private Long endSiteId;
    /**
     * 目的场地名称
     */
    private String endSiteName;

    /**
     * 操作人ERP
     */
    private String operateUserErp;
    /**
     * 操作人姓名
     */
    private String operateUserName;
    /**
     * 操作时间
     */
    private Date operateTime;

    /**
     * 派车明细编码
     */
    private String transWorkItemCode;

    /**
     * 任务状态；0-等待初始，1-在途，2-待解，3-待卸，4-卸车，5-卸车完成，6-取消
     */
    private Integer vehicleStatus;

    public String getBizId() {
        return bizId;
    }

    public void setBizId(String bizId) {
        this.bizId = bizId;
    }

    public String getSealCarCode() {
        return sealCarCode;
    }

    public void setSealCarCode(String sealCarCode) {
        this.sealCarCode = sealCarCode;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public Long getStartSiteId() {
        return startSiteId;
    }

    public void setStartSiteId(Long startSiteId) {
        this.startSiteId = startSiteId;
    }

    public String getStartSiteName() {
        return startSiteName;
    }

    public void setStartSiteName(String startSiteName) {
        this.startSiteName = startSiteName;
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

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(Integer vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }
}
