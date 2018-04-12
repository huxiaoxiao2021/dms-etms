package com.jd.bluedragon.distribution.half.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class PackageHalfApproveDto  implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 京东订单号 */
    private String orderId;

    /** 运单标识 */
    private String waybillSign;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * eclp反馈时间
     */
    private Date operateTime;

    /**
     * 操作站点ID
     */
    private Integer operateSiteId;

    /**
     * 操作站点名称
     */
    private String operateSiteName;

    /**
     * 操作人Id
     */
    private Integer operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 部分签收运单包裹同步参数集合
     */
    private List<PackageHalfApproveDetailDto> packagePartMsgDTOList;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Date getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(Date operateTime) {
        this.operateTime = operateTime;
    }

    public Integer getOperateSiteId() {
        return operateSiteId;
    }

    public void setOperateSiteId(Integer operateSiteId) {
        this.operateSiteId = operateSiteId;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperatorId() {
        return operatorId;
    }

    public void setOperatorId(Integer operatorId) {
        this.operatorId = operatorId;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public List<PackageHalfApproveDetailDto> getPackagePartMsgDTOList() {
        return packagePartMsgDTOList;
    }

    public void setPackagePartMsgDTOList(List<PackageHalfApproveDetailDto> packagePartMsgDTOList) {
        this.packagePartMsgDTOList = packagePartMsgDTOList;
    }
}
