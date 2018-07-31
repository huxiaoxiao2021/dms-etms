package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

/**
 * 面单打印信息
 * @ClassName: BasePrintWaybill
 * @Description: 面单打印信息
 * @author: wuyoude
 * @date: 2017年8月24日 下午3:13:49
 *
 */
public class BasePrintWaybill implements Serializable {
    /**始发分拣中心编码*/
    private Integer originalDmsCode;

    /**始发分拣中心名称*/
    private String originalDmsName;
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    /**
     * B商家ID
     */
    private Integer busiId ;
    /**
     * B商家名称
     */
    private String busiName ;
	/**始发城市编码*/
    private Integer originalCityCode;
    /**始发城市名称*/
    private String originalCityName; 
    /**
     * 运输产品
     */
    private String transportMode;
    /**
     * 保价标识
     */
    private Integer priceProtectFlag;
    /**
     * 保价标识信息
     */
    private String priceProtectText;
    /**
     * 签单返还信息
     */
    private String signBackText;
    /**
     * 配送类型
     */
    private String distributTypeText;
    /**
     * 寄件人
     */
    private String consigner;
    /**
     * 寄件人电话
     */
    private String consignerTel;
    /**
     * 寄件人手机号
     */
    private String consignerMobile;
    /**
     * 寄件人地址
     */
    private String consignerAddress;

	private String busiOrderCode;
    /**
    * 包裹特殊标识
    */
    private String specialMark = "";

    /**
    * 包裹特殊标识-builder类型
    */
    private StringBuilder specialMarkBuilder = new StringBuilder();
    /**
     * 收件公司名称
     */
    private String consigneeCompany;
    /**
     * 运输产品类型 waybillSign第40位，1-整车、2-快运零担、3-仓配零担
	 * waybill_sign36位=0 且waybill_sign40位=1 且 waybill_sign54位=2：冷链整车
     * waybill_sign36位=1 且waybill_sign40位=2 且 waybill_sign54位=2：快运冷链
     * waybill_sign36位=1 且waybill_sign40位=3 且 waybill_sign54位=2：仓配冷链
     */
    private String jZDFlag;

    /**
     * 寄件公司名称
     */
    private String senderCompany;
    /**
    * 路区
    */
    private String road;
    
    
    /**
    * 商家商标图像key值也就是文件名
    */
    private String brandImageKey ;
    /**
     * 标识是否SOP商家和纯外单
     */
    private boolean sopOrExternalFlg;

	/**
	 * 打印时间
	 */
	private String printTime;
	/**
	 * 北京已验视标识
	 */
	private boolean bjCheckFlg;
	/**
	 * 清真标识-1、清真 2、易污染 3、清真 易污染
	 */
	private String muslimSignText;
	/**
	 * 模板名称
	 */
	private String templateName;
	/**
	 * 模板版本-默认为0，最后一个版本号
	 */
	private int templateVersion;
	/**
	 * 运费
	 */
	private String freightText;
	/**
	 * 货款
	 */
	private String goodsPaymentText;
	/**
	 * 条码号-orderCode不为空取orderCode，否则取WaybillCode，都为空则NO-DATA
	 */
	private String barCode;
	/**
	 * 拆包员号码
	 */
	private String unpackClassifyNum;
	/**
	 * 备注
	 */
	private String remark;
    
	public String getBusiOrderCode() {
		return busiOrderCode;
	}

	public void setBusiOrderCode(String busiOrderCode) {
		this.busiOrderCode = busiOrderCode;
	}

	/**
	 * @return the originalDmsCode
	 */
	public Integer getOriginalDmsCode() {
		return originalDmsCode;
	}
	/**
	 * @param originalDmsCode the originalDmsCode to set
	 */
	public void setOriginalDmsCode(Integer originalDmsCode) {
		this.originalDmsCode = originalDmsCode;
	}
	/**
	 * @return the originalDmsName
	 */
	public String getOriginalDmsName() {
		return originalDmsName;
	}
	/**
	 * @param originalDmsName the originalDmsName to set
	 */
	public void setOriginalDmsName(String originalDmsName) {
		this.originalDmsName = originalDmsName;
	}
	/**
	 * @return the busiId
	 */
	public Integer getBusiId() {
		return busiId;
	}
	/**
	 * @param busiId the busiId to set
	 */
	public void setBusiId(Integer busiId) {
		this.busiId = busiId;
	}
	/**
	 * @return the busiName
	 */
	public String getBusiName() {
		return busiName;
	}
	/**
	 * @param busiName the busiName to set
	 */
	public void setBusiName(String busiName) {
		this.busiName = busiName;
	}
	/**
	 * @return the originalCityCode
	 */
	public Integer getOriginalCityCode() {
		return originalCityCode;
	}
	/**
	 * @param originalCityCode the originalCityCode to set
	 */
	public void setOriginalCityCode(Integer originalCityCode) {
		this.originalCityCode = originalCityCode;
	}
	/**
	 * @return the originalCityName
	 */
	public String getOriginalCityName() {
		return originalCityName;
	}
	/**
	 * @param originalCityName the originalCityName to set
	 */
	public void setOriginalCityName(String originalCityName) {
		this.originalCityName = originalCityName;
	}
	/**
	 * @return the transportMode
	 */
	public String getTransportMode() {
		return transportMode;
	}
	/**
	 * @param transportMode the transportMode to set
	 */
	public void setTransportMode(String transportMode) {
		this.transportMode = transportMode;
	}
	/**
	 * @return the priceProtectFlag
	 */
	public Integer getPriceProtectFlag() {
		return priceProtectFlag;
	}
	/**
	 * @param priceProtectFlag the priceProtectFlag to set
	 */
	public void setPriceProtectFlag(Integer priceProtectFlag) {
		this.priceProtectFlag = priceProtectFlag;
	}
	/**
	 * @return the priceProtectText
	 */
	public String getPriceProtectText() {
		return priceProtectText;
	}
	/**
	 * @param priceProtectText the priceProtectText to set
	 */
	public void setPriceProtectText(String priceProtectText) {
		this.priceProtectText = priceProtectText;
	}
	/**
	 * @return the signBackText
	 */
	public String getSignBackText() {
		return signBackText;
	}
	/**
	 * @param signBackText the signBackText to set
	 */
	public void setSignBackText(String signBackText) {
		this.signBackText = signBackText;
	}
	/**
	 * @return the distributTypeText
	 */
	public String getDistributTypeText() {
		return distributTypeText;
	}
	/**
	 * @param distributTypeText the distributTypeText to set
	 */
	public void setDistributTypeText(String distributTypeText) {
		this.distributTypeText = distributTypeText;
	}
	/**
	 * @return the consigner
	 */
	public String getConsigner() {
		return consigner;
	}
	/**
	 * @param consigner the consigner to set
	 */
	public void setConsigner(String consigner) {
		this.consigner = consigner;
	}
	/**
	 * @return the consignerTel
	 */
	public String getConsignerTel() {
		return consignerTel;
	}
	/**
	 * @param consignerTel the consignerTel to set
	 */
	public void setConsignerTel(String consignerTel) {
		this.consignerTel = consignerTel;
	}
	/**
	 * @return the consignerMobile
	 */
	public String getConsignerMobile() {
		return consignerMobile;
	}
	/**
	 * @param consignerMobile the consignerMobile to set
	 */
	public void setConsignerMobile(String consignerMobile) {
		this.consignerMobile = consignerMobile;
	}
	/**
	 * @return the consignerAddress
	 */
	public String getConsignerAddress() {
		return consignerAddress;
	}
	/**
	 * @param consignerAddress the consignerAddress to set
	 */
	public void setConsignerAddress(String consignerAddress) {
		this.consignerAddress = consignerAddress;
	}

	/**
	 * @return the specialMark
	 */
	public String getSpecialMark() {
		return specialMark;
	}

	/**
	 * @param specialMark the specialMark to set
	 */
	public void setSpecialMark(String specialMark) {
		this.specialMark = specialMark;
		specialMarkBuilder = new StringBuilder(specialMark);
	}
    /**
     * 特殊标记字段追加标记，不包含时加入标记
     * @param markText
     */
    public void appendSpecialMark(String markText){
    	//标识不为空，并且不包含此标记时加入标记
    	if(markText!=null && markText.length()>0
    			&& specialMarkBuilder.indexOf(markText) < 0){
    		specialMarkBuilder.append(markText);
    		this.specialMark = specialMarkBuilder.toString();
    	}
    }
    /**
     * 处理有冲突的标记，只保留第一个标记
     * @param markText
     * @param markText1
     */
    public void dealConflictSpecialMark(String markText,String markText1){
    	//2个标记同时包含时删除标记markText1
    	if(markText!=null && markText.length()>0
    			&& markText1!=null && markText1.length()>0
    			&& specialMarkBuilder.indexOf(markText) >= 0
    			&& specialMarkBuilder.indexOf(markText1) >= 0){
    		specialMarkBuilder.deleteCharAt(specialMarkBuilder.indexOf(markText1));
    		this.specialMark = specialMarkBuilder.toString();
    	}
    }

    public String getConsigneeCompany() {
        return consigneeCompany;
    }

    public void setConsigneeCompany(String consigneeCompany) {
        this.consigneeCompany = consigneeCompany;
    }

    public String getjZDFlag() {
        return jZDFlag;
    }

    public void setjZDFlag(String jZDFlag) {
        this.jZDFlag = jZDFlag;
    }

    public String getSenderCompany() {
        return senderCompany;
    }

    public void setSenderCompany(String senderCompany) {
        this.senderCompany = senderCompany;
    }

	/**
	 * @return the road
	 */
	public String getRoad() {
		return road;
	}

	/**
	 * @param road the road to set
	 */
	public void setRoad(String road) {
		this.road = road;
	}


	public String getBrandImageKey() {
		return brandImageKey;
	}

	public void setBrandImageKey(String brandImageKey) {
		this.brandImageKey = brandImageKey;
	}

	/**
	 * @return the sopOrExternalFlg
	 */
	public boolean isSopOrExternalFlg() {
		return sopOrExternalFlg;
	}

	/**
	 * @param sopOrExternalFlg the sopOrExternalFlg to set
	 */
	public void setSopOrExternalFlg(boolean sopOrExternalFlg) {
		this.sopOrExternalFlg = sopOrExternalFlg;
	}


	public String getPrintTime() {
		return printTime;
	}

	public void setPrintTime(String printTime) {
		this.printTime = printTime;
	}

	/**
	 * @return the bjCheckFlg
	 */
	public boolean isBjCheckFlg() {
		return bjCheckFlg;
	}

	/**
	 * @param bjCheckFlg the bjCheckFlg to set
	 */
	public void setBjCheckFlg(boolean bjCheckFlg) {
		this.bjCheckFlg = bjCheckFlg;
	}

	/**
	 * @return the muslimSignText
	 */
	public String getMuslimSignText() {
		return muslimSignText;
	}

	/**
	 * @param muslimSignText the muslimSignText to set
	 */
	public void setMuslimSignText(String muslimSignText) {
		this.muslimSignText = muslimSignText;
	}

	/**
	 * @return the templateName
	 */
	public String getTemplateName() {
		return templateName;
	}

	/**
	 * @param templateName the templateName to set
	 */
	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	/**
	 * @return the templateVersion
	 */
	public int getTemplateVersion() {
		return templateVersion;
	}

	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(int templateVersion) {
		this.templateVersion = templateVersion;
	}

	/**
	 * @return the freightText
	 */
	public String getFreightText() {
		return freightText;
	}

	/**
	 * @param freightText the freightText to set
	 */
	public void setFreightText(String freightText) {
		this.freightText = freightText;
	}

	/**
	 * @return the goodsPaymentText
	 */
	public String getGoodsPaymentText() {
		return goodsPaymentText;
	}

	/**
	 * @param goodsPaymentText the goodsPaymentText to set
	 */
	public void setGoodsPaymentText(String goodsPaymentText) {
		this.goodsPaymentText = goodsPaymentText;
	}

	/**
	 * @return the remark
	 */
	public String getRemark() {
		return remark;
	}

	/**
	 * @param remark the remark to set
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}
	/**
	 * 追加备注信息
	 * @param str
	 */
	public void appendRemark(String str) {
		if(str != null){
			if(this.remark == null){
				this.remark = str;
	    	}else{
	    		this.remark += str;
	    	}
		}
	}

	/**
	 * @return the barCode
	 */
	public String getBarCode() {
		return barCode;
	}

	/**
	 * @param barCode the barCode to set
	 */
	public void setBarCode(String barCode) {
		this.barCode = barCode;
	}

	public String getUnpackClassifyNum() {
		return unpackClassifyNum;
	}

	public void setUnpackClassifyNum(String unpackClassifyNum) {
		this.unpackClassifyNum = unpackClassifyNum;
	}
}
