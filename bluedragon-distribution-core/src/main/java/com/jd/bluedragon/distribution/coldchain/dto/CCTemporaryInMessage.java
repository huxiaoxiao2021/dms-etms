package com.jd.bluedragon.distribution.coldchain.dto;

/**
 * 冷链入库出库消息体
 * 冷链消费，分拣生产
 */
public class CCTemporaryInMessage {

    /**
     * 运单号
     */
    private String waybillNo;

    /**
     * 包裹号
     */
    private String packageNo;

    /**
     * 操作站点id
     */
    private String operateId;

    /**
     * 操作站点名称
     */
    private String operateName;

    /**
     * 操作时间 格式yyyy-MM-dd HH:mm:ss
     */
    private String tempscTime;

    /**
     * 操作人erp
     */
    private String operateErp;

    /**
     * 操作人名称
     */
    private String operateUser;

    /**
     * waybillSign
     */
    private String waybillSign;


    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public String getPackageNo() {
        return packageNo;
    }

    public void setPackageNo(String packageNo) {
        this.packageNo = packageNo;
    }

    public String getOperateId() {
        return operateId;
    }

    public void setOperateId(String operateId) {
        this.operateId = operateId;
    }

    public String getOperateName() {
        return operateName;
    }

    public void setOperateName(String operateName) {
        this.operateName = operateName;
    }

    public String getTempscTime() {
        return tempscTime;
    }

    public void setTempscTime(String tempscTime) {
        this.tempscTime = tempscTime;
    }

    public String getOperateErp() {
        return operateErp;
    }

    public void setOperateErp(String operateErp) {
        this.operateErp = operateErp;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }
}
