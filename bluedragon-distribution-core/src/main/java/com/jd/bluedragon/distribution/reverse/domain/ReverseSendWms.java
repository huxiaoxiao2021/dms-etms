package com.jd.bluedragon.distribution.reverse.domain;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name = "ReturnRequest")
public class ReverseSendWms {

	private String sendCode;

	private String operateTime;

	private String userName;
	/**
	 * 订单号
	 */
	private String orderId;
	/**
	 * 新包裹号
	 */
	private String packageCodes;

	private int lossQuantity;

	private List<Product> proList;

	private Integer cky2;

	private Integer storeId;

	private Integer orgId;

	private Integer type = -1;
	
	private Integer isInStore;

    private String waybillSign;

    private String venderId;

    private int xniType;

	private ExtInfos extInfos;

	/**
	 * 逆向编码，标识是用户退货，还是返仓再生产
	 * @return
	 */
	private Integer reverseCode;
	
	/**
	 * 逆向原因
	 * @return
	 */
	private String reverseReason;
	
    private String sourceCode;
	/**
	 * 运单号
	 */
	private String busiOrderCode;

	private boolean isSickWaybill;//true 病单， false 非病单

	private String token;//防重码 取 task_send 表task_id

	private String orderSource; // 订单来源

	private String spareColumn3; //特殊字段3 存放运单中对应的spareColumn3 返回仓储时不生成XML节点 仅供内部使用

	public final static String ORDER_SOURCE_JLZX = "JLZX"; //订单来源 加履中心

    public String getBusiOrderCode() {
		return busiOrderCode;
	}

	public void setBusiOrderCode(String busiOrderCode) {
		this.busiOrderCode = busiOrderCode;
	}

	@XmlTransient
    public Integer getReverseCode() {
		return reverseCode;
	}

	public void setReverseCode(Integer reverseCode) {
		this.reverseCode = reverseCode;
	}

	@XmlTransient
	public String getReverseReason() {
		return reverseReason;
	}

	public void setReverseReason(String reverseReason) {
		this.reverseReason = reverseReason;
	}

	@XmlTransient
    public String getSourceCode(){return sourceCode;}

    public void setSourceCode(String sourceCode){this.sourceCode=sourceCode;}
    @XmlTransient
    public String getWaybillSign() {
        return waybillSign;
    }

    public void setWaybillSign(String waybillSign) {
        this.waybillSign = waybillSign;
    }

    public Integer getIsInStore() {
		return isInStore;
	}

	public void setIsInStore(Integer isInStore) {
		this.isInStore = isInStore;
	}

	@XmlTransient
	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getCky2() {
		return cky2;
	}

	public void setCky2(Integer cky2) {
		this.cky2 = cky2;
	}

	public Integer getStoreId() {
		return storeId;
	}

	public void setStoreId(Integer storeId) {
		this.storeId = storeId;
	}

	public Integer getOrgId() {
		return orgId;
	}

	public void setOrgId(Integer orgId) {
		this.orgId = orgId;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getOrderId() {
		return orderId;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public String getPackageCodes() {
		return packageCodes;
	}

	public void setPackageCodes(String packageCodes) {
		this.packageCodes = packageCodes;
	}

	public int getLossQuantity() {
		return lossQuantity;
	}

	public void setLossQuantity(int lossQuantity) {
		this.lossQuantity = lossQuantity;
	}

	@XmlElementWrapper(name = "productRequests")
	@XmlElement(name = "ProductRequest")
	public List<Product> getProList() {
		return proList;
	}

	public void setProList(List<Product> proList) {
		this.proList = proList;
	}

	public boolean isSickWaybill() {
		return isSickWaybill;
	}

	public void setSickWaybill(boolean sickWaybill) {
		isSickWaybill = sickWaybill;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getOrderSource() {
		return orderSource;
	}

	public void setOrderSource(String orderSource) {
		this.orderSource = orderSource;
	}

	public String getVenderId() {
		return venderId;
	}

	public void setVenderId(String venderId) {
		this.venderId = venderId;
	}

	public int getXniType() {
		return xniType;
	}

	public void setXniType(int xniType) {
		this.xniType = xniType;
	}
	@XmlTransient
	public String getSpareColumn3() {
		return spareColumn3;
	}

	public void setSpareColumn3(String spareColumn3) {
		this.spareColumn3 = spareColumn3;
	}
}
