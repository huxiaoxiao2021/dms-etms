package com.jd.bluedragon.distribution.inspection.domain;

import java.io.Serializable;
import java.util.Date;

/**
 * @ClassName InspectionPackageMQ
 * @Description 包裹验货消息
 * @Author wyh
 * @Date 2020/11/12 15:07
 **/
public class InspectionPackageMQ implements Serializable {

    private static final long serialVersionUID = 5868424233298803588L;

    /**
     * 包裹号
     */
    private String packageCode;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 箱号
     */
    private String boxCode;

    /**
     * 操作人ID
     */
    private Integer operateUserId;

    /**
     * 操作人
     */
    private String operateUser;

    /**
     * 验货时间
     */
    private Date inspectionTime;

    /**
     * 操作分拣中心
     */
    private Integer operateSiteCode;

    /**
     * 验货类型
     */
    private Integer inspectionType;

    /**
     * 操作类型
     */
    private Integer operateType;

    /**
     * 收货分拣中心
     */
    private Integer receiveSiteCode;

    public String getPackageCode() {
        return packageCode;
    }

    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }

    public String getBoxCode() {
        return boxCode;
    }

    public void setBoxCode(String boxCode) {
        this.boxCode = boxCode;
    }

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUser() {
        return operateUser;
    }

    public void setOperateUser(String operateUser) {
        this.operateUser = operateUser;
    }

    public Date getInspectionTime() {
        return inspectionTime;
    }

    public void setInspectionTime(Date inspectionTime) {
        this.inspectionTime = inspectionTime;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public Integer getInspectionType() {
        return inspectionType;
    }

    public void setInspectionType(Integer inspectionType) {
        this.inspectionType = inspectionType;
    }

    public Integer getOperateType() {
        return operateType;
    }

    public void setOperateType(Integer operateType) {
        this.operateType = operateType;
    }

    public Integer getReceiveSiteCode() {
        return receiveSiteCode;
    }

    public void setReceiveSiteCode(Integer receiveSiteCode) {
        this.receiveSiteCode = receiveSiteCode;
    }
}
