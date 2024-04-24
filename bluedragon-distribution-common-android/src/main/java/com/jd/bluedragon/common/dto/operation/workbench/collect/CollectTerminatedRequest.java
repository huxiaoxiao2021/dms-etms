package com.jd.bluedragon.common.dto.operation.workbench.collect;

import java.io.Serializable;

/**
 * 揽收终止请求体
 *
 * @author hujiping
 * @date 2024/3/4 3:10 PM
 */
public class CollectTerminatedRequest implements Serializable {
    
    private static final long serialVersionUID = 8335125211595678157L;

    /**
     * 运单号
     */
    private String waybillCode;

    /**
     * 操作场地ID
     */
    private Integer operateSiteCode;

    /**
     * 操作场地名称
     */
    private String operateSiteName;

    /**
     * 操作人ID
     */
    private Integer operateUserId;
    
    /**
     * 操作人名称
     */
    private String operateUserName;

    /**
     * 揽收终止原因编码
     */
    private String terminatedReasonCode;

    /**
     * 揽收终止原因
     */
    private String terminatedReasonName;

    public String getWaybillCode() {
        return waybillCode;
    }

    public void setWaybillCode(String waybillCode) {
        this.waybillCode = waybillCode;
    }

    public Integer getOperateSiteCode() {
        return operateSiteCode;
    }

    public void setOperateSiteCode(Integer operateSiteCode) {
        this.operateSiteCode = operateSiteCode;
    }

    public String getOperateSiteName() {
        return operateSiteName;
    }

    public void setOperateSiteName(String operateSiteName) {
        this.operateSiteName = operateSiteName;
    }

    public Integer getOperateUserId() {
        return operateUserId;
    }

    public void setOperateUserId(Integer operateUserId) {
        this.operateUserId = operateUserId;
    }

    public String getOperateUserName() {
        return operateUserName;
    }

    public void setOperateUserName(String operateUserName) {
        this.operateUserName = operateUserName;
    }

    public String getTerminatedReasonCode() {
        return terminatedReasonCode;
    }

    public void setTerminatedReasonCode(String terminatedReasonCode) {
        this.terminatedReasonCode = terminatedReasonCode;
    }

    public String getTerminatedReasonName() {
        return terminatedReasonName;
    }

    public void setTerminatedReasonName(String terminatedReasonName) {
        this.terminatedReasonName = terminatedReasonName;
    }
}
