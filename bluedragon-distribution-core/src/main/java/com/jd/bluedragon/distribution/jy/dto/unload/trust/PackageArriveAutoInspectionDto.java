package com.jd.bluedragon.distribution.jy.dto.unload.trust;

import java.io.Serializable;
import java.util.Date;

/**
 * @Author zhengchengfa
 * @Date 2024/2/29 21:39
 * @Description
 */
public class PackageArriveAutoInspectionDto extends MqRetryBaseDto implements Serializable {
    static final long serialVersionUID = 1L;

    private String packageCode;
    private String waybillCode;
    //委托书编码	: TMS系统全局唯一
    private String transBookCode;
    //派车任务编码
    private String transWorkCode;
    //派车任务明细编码
    private String transWorkItemCode;
    //到达网点编号
    private String arriveSiteCode;
    private String arriveSiteName;
    private Integer arriveSiteId;
    //操作时间
    private Date operateTime;
    //消息发送时间
    private Date createTime;



    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public String getTransBookCode() {
        return transBookCode;
    }

    public void setTransBookCode(String transBookCode) {
        this.transBookCode = transBookCode;
    }

    public String getTransWorkCode() {
        return transWorkCode;
    }

    public void setTransWorkCode(String transWorkCode) {
        this.transWorkCode = transWorkCode;
    }

    public String getTransWorkItemCode() {
        return transWorkItemCode;
    }

    public void setTransWorkItemCode(String transWorkItemCode) {
        this.transWorkItemCode = transWorkItemCode;
    }

    public String getArriveSiteCode() {
        return arriveSiteCode;
    }

    public void setArriveSiteCode(String arriveSiteCode) {
        this.arriveSiteCode = arriveSiteCode;
    }

    public String getArriveSiteName() {
        return arriveSiteName;
    }

    public void setArriveSiteName(String arriveSiteName) {
        this.arriveSiteName = arriveSiteName;
    }

    public Integer getArriveSiteId() {
        return arriveSiteId;
    }

    public void setArriveSiteId(Integer arriveSiteId) {
        this.arriveSiteId = arriveSiteId;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

}
