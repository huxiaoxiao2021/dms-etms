package com.jd.bluedragon.distribution.api.request;

import javax.xml.bind.annotation.XmlRootElement;

import com.jd.bluedragon.distribution.api.JdRequest;

@XmlRootElement(name = "RejectRequest")
public class RejectRequest extends JdRequest {
    
    private static final long serialVersionUID = 1L;
    
    /** 创建站点编号 */
    private Integer createSiteCode;
    
    /** 创建站点名称 */
    private String createSiteName;
    
    /** 机构ID */
    private Integer orgId;
    
    /** 配送中心标识 */
    private Integer cky2;
    
    /** 库房ID */
    private Integer storeId;
    
    /** 订单编号 */
    private String orderId;
    
    /** 取件单号 */
    private String pickwareCode;
    
    /** 包裹编号 */
    private String packageCode;
    
    /** 操作人编号 */
    private String operatorCode;
    
    /** 操作人 */
    private String operator;
    
    /** 验货人 */
    private String inspector;
    
    /** 验货人编号 */
    private String inspectorCode;
    
    /** 验货时间 */
    private String inspectTime;
    
    /** 时候数量 */
    private Integer actualPackageQuantity;
    
    /**表示来源 PRIME,ECLP 两种,但不消费ECLP的消息**/
    private String sorceCode;
    
    /**备件库拒收原因
     * 1=配送少包裹
     * 2=配送包裹破损
     * 3=配送少发票
     * 4=仓库包裹少货
     * 5=仓库包裹破损
     * 6=仓库包裹丢失
     * 7=超期拒收
     */
    private String rejectReason;
    
    public Integer getCreateSiteCode() {
        return this.createSiteCode;
    }
    
    public void setCreateSiteCode(Integer createSiteCode) {
        this.createSiteCode = createSiteCode;
    }
    
    public String getCreateSiteName() {
        return this.createSiteName;
    }
    
    public void setCreateSiteName(String createSiteName) {
        this.createSiteName = createSiteName;
    }
    
    public Integer getOrgId() {
        return this.orgId;
    }
    
    public void setOrgId(Integer orgId) {
        this.orgId = orgId;
    }
    
    public Integer getCky2() {
        return this.cky2;
    }
    
    public void setCky2(Integer cky2) {
        this.cky2 = cky2;
    }
    
    public Integer getStoreId() {
        return this.storeId;
    }
    
    public void setStoreId(Integer storeId) {
        this.storeId = storeId;
    }
    
    public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPickwareCode() {
        return this.pickwareCode;
    }
    
    public void setPickwareCode(String pickwareCode) {
        this.pickwareCode = pickwareCode;
    }
    
    public String getPackageCode() {
        return this.packageCode;
    }
    
    public void setPackageCode(String packageCode) {
        this.packageCode = packageCode;
    }
    
    public String getOperatorCode() {
        return this.operatorCode;
    }
    
    public void setOperatorCode(String operatorCode) {
        this.operatorCode = operatorCode;
    }
    
    public String getOperator() {
        return this.operator;
    }
    
    public void setOperator(String operator) {
        this.operator = operator;
    }
    
    public String getInspector() {
        return this.inspector;
    }
    
    public void setInspector(String inspector) {
        this.inspector = inspector;
    }
    
    public String getInspectorCode() {
        return this.inspectorCode;
    }
    
    public void setInspectorCode(String inspectorCode) {
        this.inspectorCode = inspectorCode;
    }
    
    public String getInspectTime() {
        return this.inspectTime;
    }
    
    public void setInspectTime(String inspectTime) {
        this.inspectTime = inspectTime;
    }
    
    public Integer getActualPackageQuantity() {
        return this.actualPackageQuantity;
    }
    
    public void setActualPackageQuantity(Integer actualPackageQuantity) {
        this.actualPackageQuantity = actualPackageQuantity;
    }

	public String getSorceCode() {
		return sorceCode;
	}

	public void setSorceCode(String sorceCode) {
		this.sorceCode = sorceCode;
	}

	public String getRejectReason() {
		return rejectReason;
	}

	public void setRejectReason(String rejectReason) {
		this.rejectReason = rejectReason;
	}
    
}
