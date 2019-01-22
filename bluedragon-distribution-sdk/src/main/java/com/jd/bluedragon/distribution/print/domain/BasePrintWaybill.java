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

	/**运单号*/
	private String waybillCode;

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
	 * 商家编码
	 */
	private String busiCode;
    /**
     * B商家名称
     */
    private String busiName ;
	/**始发城市编码*/
    private Integer originalCityCode;
    /**始发城市名称*/
    private String originalCityName;
    /**
     * 始发滑道号类型
     */
    private Integer originalCrossType;
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
     * 商家别名：YHD(一号店)、 CMBC(招商银行)
     */
    private String dmsBusiAlias;
    /**
    * 商家商标图像key值也就是文件名
    */
    private String brandImageKey;
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
	private Integer templateVersion = 0;
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

	/**
	 * B网面单已称标识
	 */
	private String weightFlagText;

	/**
	 * 客户预约时间
	 */
	private String customerOrderTime;

	/**
	 * 派送时段
	 */
	private String deliveryTimeCategory;

	/**
	 * 特殊要求
	 */
	private String specialRequirement;

	/**
	 * B网面单备用站点id
	 */
	private Integer backupSiteId;

	/**
	 * B网面单备用站点名称
	 */
	private String backupSiteName;

	/**标签打印地址*/
	private String printAddress;

	/**
	 * 客户姓名
	 */
	private String customerName ;

	/**客户联系方式*/
	private String customerContacts;

	/**
	 * 客户联系方式 tmsWaybill.getReceiverMobile(),tmsWaybill.getReceiverTel()
	 */
	private String mobileFirst;
	private String mobileLast;

	private String telFirst;
	private String telLast;

	/**
	 * 京东logo图片路径
	 */
	private String jdLogoImageKey;

	/**
	 * 二维码内容
	 */
	private String popularizeMatrixCode;

	/**
	 * 已验视
	 * @param waybillCode
	 */
	private String examineFlag;

	/**
	 * 已安检
	 */
	private String securityCheck;

	/**
	 * 派送方式 【送】【提】
	 */
	private String deliveryMethod;

	/**
	 * 运单号前几位
	 */
	private String waybillCodeFirst;

	/**
	 * 运单号后4位
	 */
	private String waybillCodeLast;

	/**
	 * 包裹号索引
	 */
	private String packageIndexUnderLine;

	/**
	 * 路由节点城市名称
	 */
	private String routerNode1;
	private String routerNode2;
	private String routerNode3;
	private String routerNode4;
	private String routerNode5;
	private String routerNode6;
	private String routerNode7;
	private String routerNode8;



	public BasePrintWaybill(String waybillCode){
		this.waybillCode = waybillCode;
	}

	public BasePrintWaybill(){

	}


	/**
	 * 京东物流网址和客服电话
	 * */
	private String additionalComment;

	public String getAdditionalComment() {
		return additionalComment;
	}

	public void setAdditionalComment(String additionalComment) {
		this.additionalComment = additionalComment;
	}

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
	 * @return the originalCrossType
	 */
	public Integer getOriginalCrossType() {
		return originalCrossType;
	}

	/**
	 * @param originalCrossType the originalCrossType to set
	 */
	public void setOriginalCrossType(Integer originalCrossType) {
		this.originalCrossType = originalCrossType;
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


	/**
	 * @return the dmsBusiAlias
	 */
	public String getDmsBusiAlias() {
		return dmsBusiAlias;
	}

	/**
	 * @param dmsBusiAlias the dmsBusiAlias to set
	 */
	public void setDmsBusiAlias(String dmsBusiAlias) {
		this.dmsBusiAlias = dmsBusiAlias;
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
	public Integer getTemplateVersion() {
		return templateVersion;
	}

	/**
	 * @param templateVersion the templateVersion to set
	 */
	public void setTemplateVersion(Integer templateVersion) {
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

	public String getBackupSiteName() {
		return backupSiteName;
	}

	public void setBackupSiteName(String backupSiteName) {
		this.backupSiteName = backupSiteName;
	}

	public String getSpecialRequirement() {
		return specialRequirement;
	}

	public void setSpecialRequirement(String specialRequirement) {
		this.specialRequirement = specialRequirement;
	}

	public String getCustomerOrderTime() {
		return customerOrderTime;
	}

	public void setCustomerOrderTime(String customerOrderTime) {
		this.customerOrderTime = customerOrderTime;
	}

	public String getDeliveryTimeCategory() {
		return deliveryTimeCategory;
	}

	public void setDeliveryTimeCategory(String deliveryTimeCategory) {
		this.deliveryTimeCategory = deliveryTimeCategory;
	}

	public static long getSerialVersionUID() {
		return serialVersionUID;
	}

	public String getWeightFlagText() {
		return weightFlagText;
	}

	public void setWeightFlagText(String weightFlagText) {
		this.weightFlagText = weightFlagText;
	}


	public String getBusiCode() {
		return busiCode;
	}

	public void setBusiCode(String busiCode) {
		this.busiCode = busiCode;
	}

	public String getWaybillCode() {
		return waybillCode;
	}

	public void setWaybillCode(String waybillCode) {
		this.waybillCode = waybillCode;
	}

	public String getPrintAddress() {
		return printAddress;
	}

	public void setPrintAddress(String printAddress) {
		this.printAddress = printAddress;
	}

	public String getCustomerName() {
		return customerName;
	}

	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}

	public String getCustomerContacts() {
		return customerContacts;
	}

	public void setCustomerContacts(String customerContacts) {
		this.customerContacts = customerContacts;
	}

	public String getMobileFirst() {
		return mobileFirst;
	}

	public void setMobileFirst(String mobileFirst) {
		this.mobileFirst = mobileFirst;
	}

	public String getMobileLast() {
		return mobileLast;
	}

	public void setMobileLast(String mobileLast) {
		this.mobileLast = mobileLast;
	}

	public String getTelFirst() {
		return telFirst;
	}

	public void setTelFirst(String telFirst) {
		this.telFirst = telFirst;
	}

	public String getTelLast() {
		return telLast;
	}

	public void setTelLast(String telLast) {
		this.telLast = telLast;
	}

	public Integer getBackupSiteId() {
		return backupSiteId;
	}

	public void setBackupSiteId(Integer backupSiteId) {
		this.backupSiteId = backupSiteId;
	}

	public String getJdLogoImageKey() {
		return jdLogoImageKey;
	}

	public void setJdLogoImageKey(String jdLogoImageKey) {
		this.jdLogoImageKey = jdLogoImageKey;
	}

	public String getPopularizeMatrixCode() {
		return popularizeMatrixCode;
	}

	public void setPopularizeMatrixCode(String popularizeMatrixCode) {
		this.popularizeMatrixCode = popularizeMatrixCode;
	}

	public String getExamineFlag() {
		return examineFlag;
	}

	public void setExamineFlag(String examineFlag) {
		this.examineFlag = examineFlag;
	}

	public String getSecurityCheck() {
		return securityCheck;
	}

	public void setSecurityCheck(String securityCheck) {
		this.securityCheck = securityCheck;
	}

	public String getDeliveryMethod() {
		return deliveryMethod;
	}

	public void setDeliveryMethod(String deliveryMethod) {
		this.deliveryMethod = deliveryMethod;
	}

	public String getWaybillCodeFirst() {
		return waybillCodeFirst;
	}

	public void setWaybillCodeFirst(String waybillCodeFirst) {
		this.waybillCodeFirst = waybillCodeFirst;
	}

	public String getWaybillCodeLast() {
		return waybillCodeLast;
	}

	public void setWaybillCodeLast(String waybillCodeLast) {
		this.waybillCodeLast = waybillCodeLast;
	}

	public String getPackageIndexUnderLine() {
		return packageIndexUnderLine;
	}

	public void setPackageIndexUnderLine(String packageIndexUnderLine) {
		this.packageIndexUnderLine = packageIndexUnderLine;
	}

	public String getRouterNode1() {
		return routerNode1;
	}

	public void setRouterNode1(String routerNode1) {
		this.routerNode1 = routerNode1;
	}

	public String getRouterNode2() {
		return routerNode2;
	}

	public void setRouterNode2(String routerNode2) {
		this.routerNode2 = routerNode2;
	}

	public String getRouterNode3() {
		return routerNode3;
	}

	public void setRouterNode3(String routerNode3) {
		this.routerNode3 = routerNode3;
	}

	public String getRouterNode4() {
		return routerNode4;
	}

	public void setRouterNode4(String routerNode4) {
		this.routerNode4 = routerNode4;
	}

	public String getRouterNode5() {
		return routerNode5;
	}

	public void setRouterNode5(String routerNode5) {
		this.routerNode5 = routerNode5;
	}

	public String getRouterNode6() {
		return routerNode6;
	}

	public void setRouterNode6(String routerNode6) {
		this.routerNode6 = routerNode6;
	}

	public String getRouterNode7() {
		return routerNode7;
	}

	public void setRouterNode7(String routerNode7) {
		this.routerNode7 = routerNode7;
	}

	public String getRouterNode8() {
		return routerNode8;
	}

	public void setRouterNode8(String routerNode8) {
		this.routerNode8 = routerNode8;
	}
}
