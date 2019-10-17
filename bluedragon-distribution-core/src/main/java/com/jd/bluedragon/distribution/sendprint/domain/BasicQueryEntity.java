package com.jd.bluedragon.distribution.sendprint.domain;

import java.io.Serializable;

public class BasicQueryEntity implements Serializable{
	
    private static final long serialVersionUID = 1063950745834027247L;

    /** 分拣中心 */
	private String sendSiteName;
	
	/** 目的站点 */
	private String receiveSiteName;
	
	/** 目的站点编号 */
	private Integer receiveSiteCode;
	
	/** 目的站点类型 */
	private Integer receiveSiteType;
	
	/** 订单站点 */
	private String siteName;
	
	/** 订单站点编号 */
	private Integer siteCode;
	
	/** 订单站点类型 */
	private String siteType;
	
	/** 交接批次 */
	private String sendCode;
	
	/** 箱号 */
	private String boxCode;
	
	/** 库房号 */
	private Integer fcNo;
	
	/** 订单号 */
	private String waybill;
	
	/** 包裹数量 */
	private Integer packageBarNum;
	
	/** 包裹号 */
	private String packageBar;

    /** 包裹体积 */
    private Double goodVolume;
	
	/** 订单重量 */
	private Double goodWeight;
	
	/** 包裹重量 */
	private Double packageBarWeight;
	
	/** 订单复重量 */
	private Double goodWeight2;
	
	/** 包裹重量 */
	private Double packageBarWeight2;
	
	/** 支付方式ID */
	private Integer payment;
	
	/** 订单支付类型 */
	private String sendPay;
	
	/** 订单类型 */
	private String waybillType;
	
	/** 应收金额 */
	private String declaredValue;
	
	/** 商品金额 */
	private String goodValue;
	
	/** 发货人 */
	private String sendUser;
	
	/** 发货人ID */
	private Integer sendUserCode;
	
	/** 客户姓名 */
	private String receiverName;
	
	/** 客户地址 */
	private String receiverAddress;
	
	/** 客户电话 */
	private String receiverMobile;
	
	/** 上门换新*/
    private String isnew;
    
    /** 删除状态*/
    private String iscancel;
    
    /** 发票号*/
    private String invoice;
    
    /** 封签号*/
    private String sealNo;
    
    /** 奢侈品*/
    private String luxury;
    
    /** 返修号*/
    private String reworkNo;

	/** 发货时间*/
	private String operateTime;


	/** 托盘号*/
	private String boardCode;

	/** 托盘体积*/
	private Double boardVolume;

	/**
	 * 分拣中心应付自动量方体积
	 */
	private Double dmsOutVolumeDynamic;

	/**
	 * 分拣中心应付人工量方体积
	 */
	private Double dmsOutVolumeStatic;

    /**
     * 路区号
     */
	private String roadCode;

    /**
     * 封车时间
     */
	private String sealTime;

	/**
	 * 是否是211时效订单 0：否，1：是
	 */
	private Integer is211;

	public Double getGoodWeight2() {
		return goodWeight2;
	}

	public void setGoodWeight2(Double goodWeight2) {
		this.goodWeight2 = goodWeight2;
	}

	public String getSendSiteName() {
		return sendSiteName;
	}

	public void setSendSiteName(String sendSiteName) {
		this.sendSiteName = sendSiteName;
	}

	public String getReceiveSiteName() {
		return receiveSiteName;
	}

	public void setReceiveSiteName(String receiveSiteName) {
		this.receiveSiteName = receiveSiteName;
	}

	public Integer getReceiveSiteCode() {
		return receiveSiteCode;
	}

	public void setReceiveSiteCode(Integer receiveSiteCode) {
		this.receiveSiteCode = receiveSiteCode;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public Integer getSiteCode() {
		return siteCode;
	}

	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	public String getSendCode() {
		return sendCode;
	}

	public void setSendCode(String sendCode) {
		this.sendCode = sendCode;
	}

	public String getBoxCode() {
		return boxCode;
	}

	public void setBoxCode(String boxCode) {
		this.boxCode = boxCode;
	}

	public Integer getFcNo() {
		return fcNo;
	}

	public void setFcNo(Integer fcNo) {
		this.fcNo = fcNo;
	}

	public String getWaybill() {
		return waybill;
	}

	public void setWaybill(String waybill) {
		this.waybill = waybill;
	}

	public Integer getPackageBarNum() {
		return packageBarNum;
	}

	public void setPackageBarNum(Integer packageBarNum) {
		this.packageBarNum = packageBarNum;
	}

	public String getPackageBar() {
		return packageBar;
	}

	public void setPackageBar(String packageBar) {
		this.packageBar = packageBar;
	}

	public Double getGoodWeight() {
		return goodWeight;
	}

	public void setGoodWeight(Double goodWeight) {
		this.goodWeight = goodWeight;
	}

	public Double getPackageBarWeight() {
		return packageBarWeight;
	}

	public void setPackageBarWeight(Double packageBarWeight) {
		this.packageBarWeight = packageBarWeight;
	}

	public Integer getPayment() {
		return payment;
	}

	public void setPayment(Integer payment) {
		this.payment = payment;
	}

    public String getSendPay() {
        return sendPay;
    }

    public void setSendPay(String sendPay) {
        this.sendPay = sendPay;
    }

    public String getIsnew() {
        return isnew;
    }

    public void setIsnew(String isnew) {
        this.isnew = isnew;
    }

	public String getIscancel() {
        return iscancel;
    }

    public void setIscancel(String iscancel) {
        this.iscancel = iscancel;
    }

    public String getWaybillType() {
        return waybillType;
    }

    public void setWaybillType(String waybillType) {
        this.waybillType = waybillType;
    }

    public String getDeclaredValue() {
        return declaredValue;
    }

    public void setDeclaredValue(String declaredValue) {
        this.declaredValue = declaredValue;
    }

    public String getSendUser() {
		return sendUser;
	}

	public void setSendUser(String sendUser) {
		this.sendUser = sendUser;
	}

	public Integer getSendUserCode() {
		return sendUserCode;
	}

	public void setSendUserCode(Integer sendUserCode) {
		this.sendUserCode = sendUserCode;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverAddress() {
		return receiverAddress;
	}

	public void setReceiverAddress(String receiverAddress) {
		this.receiverAddress = receiverAddress;
	}

	public String getReceiverMobile() {
		return receiverMobile;
	}

	public void setReceiverMobile(String receiverMobile) {
		this.receiverMobile = receiverMobile;
	}

	public String getGoodValue() {
		return goodValue;
	}

	public void setGoodValue(String goodValue) {
		this.goodValue = goodValue;
	}

	public Double getPackageBarWeight2() {
		return packageBarWeight2;
	}

	public void setPackageBarWeight2(Double packageBarWeight2) {
		this.packageBarWeight2 = packageBarWeight2;
	}

	public String getSiteType() {
		return siteType;
	}

	public void setSiteType(String siteType) {
		this.siteType = siteType;
	}

	public String getInvoice() {
		return invoice;
	}

	public String getSealNo() {
		return sealNo;
	}

	public void setSealNo(String sealNo) {
		this.sealNo = sealNo;
	}

	public String getLuxury() {
		return luxury;
	}

	public void setLuxury(String luxury) {
		this.luxury = luxury;
	}

	public String getReworkNo() {
		return reworkNo;
	}

	public void setReworkNo(String reworkNo) {
		this.reworkNo = reworkNo;
	}

	public void setInvoice(String invoice) {
		this.invoice = invoice;
	}

	public String getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(String operateTime) {
		this.operateTime = operateTime;
	}

	public Integer getReceiveSiteType() {
		return receiveSiteType;
	}

	public void setReceiveSiteType(Integer receiveSiteType) {
		this.receiveSiteType = receiveSiteType;
	}

    public Double getGoodVolume() {
        return goodVolume;
    }

    public void setGoodVolume(Double goodVolume) {
        this.goodVolume = goodVolume;
    }

	public String getBoardCode() {
		return boardCode;
	}

	public void setBoardCode(String boardCode) {
		this.boardCode = boardCode;
	}

	public Double getBoardVolume() {
		return boardVolume;
	}

	public void setBoardVolume(Double boardVolume) {
		this.boardVolume = boardVolume;
	}

	public Double getDmsOutVolumeDynamic() {
		return dmsOutVolumeDynamic;
	}

	public void setDmsOutVolumeDynamic(Double dmsOutVolumeDynamic) {
		this.dmsOutVolumeDynamic = dmsOutVolumeDynamic;
	}

	public Double getDmsOutVolumeStatic() {
		return dmsOutVolumeStatic;
	}

	public void setDmsOutVolumeStatic(Double dmsOutVolumeStatic) {
		this.dmsOutVolumeStatic = dmsOutVolumeStatic;
	}

	public String getRoadCode() {
		return roadCode;
	}

	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
	}

	public String getSealTime() {
		return sealTime;
	}

	public void setSealTime(String sealTime) {
		this.sealTime = sealTime;
	}

	public Integer getIs211() {
		return is211;
	}

	public void setIs211(Integer is211) {
		this.is211 = is211;
	}
}
