package com.jd.bluedragon.distribution.api.request;

import java.io.Serializable;
import java.util.List;

public class OrderBindMessageRequest implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 站点id
     */
    private Long siteCode;
    /**
     * 站点名称
     */
    private String siteName;

    /**
     * 青流箱编码集合  如果关联多个 用多个
     */
    private List<String> sealNos;

    /**
     * 操作人erp
     */
    private String operatorErp;

    /**
     * 操作人姓名
     */
    private String operatorName;

    /**
     * 操作时间 格式 2020-05-20 00:08:17
     */
    private String operateTime;

    /**
     * 订单号
     */
    private String orderNo;

    /**
     * 运单号
     */
    private String waybillNo;

    /**
     * 操作状态 39 站点揽收
     */
    private Integer cbStatus;

    public Long getSiteCode() {
        return siteCode;
    }

    public void setSiteCode(Long siteCode) {
        this.siteCode = siteCode;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public List<String> getSealNos() {
        return sealNos;
    }

    public void setSealNos(List<String> sealNos) {
        this.sealNos = sealNos;
    }

    public String getOperatorErp() {
        return operatorErp;
    }

    public void setOperatorErp(String operatorErp) {
        this.operatorErp = operatorErp;
    }

    public String getOperatorName() {
        return operatorName;
    }

    public void setOperatorName(String operatorName) {
        this.operatorName = operatorName;
    }

    public String getOperateTime() {
        return operateTime;
    }

    public void setOperateTime(String operateTime) {
        this.operateTime = operateTime;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getWaybillNo() {
        return waybillNo;
    }

    public void setWaybillNo(String waybillNo) {
        this.waybillNo = waybillNo;
    }

    public Integer getCbStatus() {
        return cbStatus;
    }

    public void setCbStatus(Integer cbStatus) {
        this.cbStatus = cbStatus;
    }
}
