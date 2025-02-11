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
	private static final long serialVersionUID = 1L;
	public BasePrintWaybill(){
	}
	public BasePrintWaybill(String waybillCode){
		this.waybillCode = waybillCode;
	}

	/**运单号*/
	private String waybillCode;

    /**始发分拣中心编码*/
    private Integer originalDmsCode;

    /**始发分拣中心名称*/
    private String originalDmsName;
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
    * 新包裹特殊标识，相比specialMark，去掉了部分标识（航、航填）
    */
    private String specialMarkNew = "";
    /**
    * 包裹特殊标识-builder类型
    */
    private StringBuilder specialMarkBuilder = new StringBuilder();
    /**
    * 新包裹特殊标识-builder类型
    */
    private StringBuilder specialMarkNewBuilder = new StringBuilder();  
    /**
     * 收件公司名称
     */
    private String consigneeCompany;
    /**
     * 运输产品类型 waybillSign第40位，1-整车、2-快运零担、3-仓配零担
	 * waybill_sign36位=0 且waybill_sign40位=1 且 waybill_sign54位=2：冷链整车
     * waybill_sign36位=1 且waybill_sign40位=2 且 waybill_sign54位=2：快运冷链
	 * 快运冷链下新增 原逻辑待业务确认是否变更
	 * {
	 *     1. Waybill_sign54=2生鲜行业 且Waybill_sign40=2 纯配快运零担 且Waybill_sign80=6 且118=2 城配整车，即为：“冷链城配整车”
	 *     2. Waybill_sign54=2生鲜行业 且 Waybill_sign40=2纯配快运零担 且Waybill_sign80=6 且 118= 1或者0或者空 城配共配，即为：“冷链城配共配”
	 *     3. Waybill_sign54=2 生鲜行业 且Waybill_sign40=2 纯配快运零担且Waybill_sign80=8 专车，代表：“冷链整车”
	 * }
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
	 * 模板版本字符串-默认为1，云打印用
	 */
	private String templateVersionStr = "1";
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

	/*
	* 二维码描述
	* */
	private String popularizeMatrixCodeDesc;

	/*
	 * 运单增值服务标识
	 * 规则：第一位：1：包裹有话说，0：无
	 * */
	private String waybillVasSign = "";

	/**
	 * 已验视
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

	/**
	 * 是否执行新的路由逻辑
	 */
	private boolean executeNewRouterLogic;
	/**
	 * 路由节点分拣代码
	 */
	private String routerSectionNo1;
	private String routerSectionNo2;
	private String routerSectionNo3;
	private String routerSectionNo4;
	private String routerSectionNo5;
	private String routerSectionNo6;
	/**
	 * 路由区间货区编码
	 */
	private String originalSectionAreaNo;
	private String routerSectionAreaNo1;
	private String routerSectionAreaNo2;
	private String routerSectionAreaNo3;
	private String routerSectionAreaNo4;
	private String routerSectionAreaNo5;
	private String routerSectionAreaNo6;
	private String destinationSectionAreaNo;


	/** 预分拣站点 **/
	private String printSiteName;

	/** 目的道口号 **/
	private String destinationCrossCode;

	/** 目的分拣中心名称 **/
	private String destinationDmsName;

	/** 目的笼车号**/
	private String destinationTabletrolleyCode;

	/** 始发笼车号 **/
	private String originalTabletrolleyCode;

	/**
	 * 包裹特殊标识同specialMark
	 */
	private String waybillSignText ;

	private String roadCode;
	/**
	 * 模板纸张大小编码
	 */
	private String templatePaperSizeCode;
	/**
	 * 使用新统一模板标识
	 */
	private Boolean useNewTemplate = Boolean.FALSE;
	/**
	 * 模板分组编码
	 */
	private String templateGroupCode;

	/**
	 * @see this.customerContacts
     */
	private String customerPhoneText;

	/**
	 * @see this.road
     */
	private String rodeCode;

	/**
	 * 寄件人信息前缀
     */
	private String consignerPrefixText;

	/**
	 * 寄件人号码信息
     */
	private String consignerTelText;

	/**
	 * 包裹特殊标识1
	 */
	private String specialMark1 = "";

	/**
	 * 包裹特殊标识1-builder类型
	 */
	private StringBuilder specialMark1Builder = new StringBuilder();

	/**
	 * 京东物流网址和客服电话
	 * */
	private String additionalComment;

	/**
	 * 传入运单时待打印的包裹列表的下标指引
	 * 用于指引客户端需要打印哪一个包裹
	 */
	private Integer willPrintPackageIndex = 0;

	/**
	 * 旧单的运单号
     */
	private String oldWaybillCode;
    /**预分拣站点Code*/
    private Integer prepareSiteCode;

    /**预分拣站点名称*/
    private String prepareSiteName;

    /**目的分拣中心编码*/
    private Integer purposefulDmsCode;

    /**目的分拣中心名称*/
    private String purposefulDmsName;

    /**始发道口*/
    private String originalCrossCode;

    /**目的道口*/
    private String purposefulCrossCode;

    /**始发笼车号*/
    private String originalTabletrolley;

    /**目的笼车号*/
    private String purposefulTableTrolley;
	/**
	 * 目的分拣中心-城市编码
	 */
	private String destinationCityDmsCode;
	/**
	 * 标签文件类型（pdf）
	 */
	private Integer labelFileType;
	/**
	 * 标签文件Url（下载路径）
	 */
	private String labelFileUrl;
    /**订单号*/
	private String orderCode;
    /**
    * 承诺配送信息
    */
    private String promiseText ;
    /**
    * 承诺配送信息-(C网模板展示)
    */
    private String promiseTextC ;
    /**
    * 时效
    */
    private String timeCategory ;

	/**
	 * 物品名称
	 */
	private String goodsName;

	/**
	 * B/C网水印标识
	 */
	private String bcSign;

	/**
	 * 其他路区（现阶段只存放大件路区）
	 */
	private String backupRoadCode;
	
	/**
	 * 地址备注信息，追加显示到面单的地址printAddress
	 */
	private String printAddressRemark;

	/**
	 * 服务号
	 */
	private String serviceCode;

	private String comment;
	/**
	 * 集货地
	 */
	private String collectionAddress;
	/**
	 * 运输方式：航、航填、高、陆
	 */
	private String transportTypeText;
	/**
	 * 京准达标识-尊
	 */
	private String respectTypeText;
	/**
	 * 代收货款:codMoney
	 */
	private String codMoneyText;
	/**
	 * 运费合计：totalCharge
	 */
	private String totalChargeText;
	/**
	 * 产品标识
	 */
	private String transportModeFlag;

    /**
     * 水印
     */
	private String waterMark;

	/**
	 * 面单输出方式
	 * @see SurfaceOutputTypeEnum
	 *  0、打印
	 *  1、预览
	 */
	private Integer outputType = 0;
	
    /**
     * aoi编码
     */
	private String aoiCode;
	/**
	 * 返调度标识 1-返调度 0-其他打印
	 */
	private Integer localSchedule;

	/**
	 * 始发滑道号类型 (临时变量)
	 */
	private Integer tempOriginalCrossType;

	/**
	 * 拼接商品名称
	 */
	private String spliceGoodsNameWithTitle;

	/**
	 * 真实验证码（取件码）
	 */
	private String verificationCodeWithTitle;

	/**
	 * 标签文件url（下载地址）
	 */
	private String labelFileDownloadUrl;

	/**
	 * 云打印打印模版所属系统
	 */
	private String templateSysName;
	
	public String getServiceCode() {
		return serviceCode;
	}

	public void setServiceCode(String serviceCode) {
		this.serviceCode = serviceCode;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

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
		if(specialMark != null){
			specialMarkBuilder = new StringBuilder(specialMark);
		}else{
			specialMarkBuilder = new StringBuilder();
		}
	}
    /**
	 * 不包含markText时追加到specialMark，同时追加到specialMarkNew
	 * @param markText
	 */
	public void appendSpecialMark(String markText){
		//追加标识到specialMark，同时追加到SpecialMarkNew
		appendSpecialMark(markText,true);
	}
    /**
     * 不包含markText时追加到specialMark，根据appendSpecialMarkNew标识判断是否追加到specialMarkNew
     * @param markText
     * @param appendSpecialMarkNew 是否追加到specialMarkNew字段
     */
	public void appendSpecialMark(String markText,boolean appendSpecialMarkNew){
		//标识不为空，并且不包含此标记时加入标记
		if(markText!=null && markText.length()>0
				&& specialMarkBuilder.indexOf(markText) < 0){
			specialMarkBuilder.append(markText);
			this.specialMark = specialMarkBuilder.toString();
			this.waybillSignText = this.specialMark;
		}
		if(appendSpecialMarkNew){
			appendSpecialMarkNew(markText);
		}
	}	
    /**
	 * 不包含markText时追加到specialMarkNew
	 * @param markText
	 */
	private void appendSpecialMarkNew(String markText){
		//标识不为空，并且不包含此标记时加入标记
		if(markText!=null && markText.length()>0
				&& specialMarkNewBuilder.indexOf(markText) < 0){
			specialMarkNewBuilder.append(markText);
			this.specialMarkNew = specialMarkNewBuilder.toString();
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
			this.waybillSignText = specialMarkBuilder.toString();
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

	public boolean getExecuteNewRouterLogic() {
		return executeNewRouterLogic;
	}

	public void setExecuteNewRouterLogic(boolean executeNewRouterLogic) {
		this.executeNewRouterLogic = executeNewRouterLogic;
	}

	public String getRouterSectionNo1() {
		return routerSectionNo1;
	}

	public void setRouterSectionNo1(String routerSectionNo1) {
		this.routerSectionNo1 = routerSectionNo1;
	}

	public String getRouterSectionNo2() {
		return routerSectionNo2;
	}

	public void setRouterSectionNo2(String routerSectionNo2) {
		this.routerSectionNo2 = routerSectionNo2;
	}

	public String getRouterSectionNo3() {
		return routerSectionNo3;
	}

	public void setRouterSectionNo3(String routerSectionNo3) {
		this.routerSectionNo3 = routerSectionNo3;
	}

	public String getRouterSectionNo4() {
		return routerSectionNo4;
	}

	public void setRouterSectionNo4(String routerSectionNo4) {
		this.routerSectionNo4 = routerSectionNo4;
	}

	public String getRouterSectionNo5() {
		return routerSectionNo5;
	}

	public void setRouterSectionNo5(String routerSectionNo5) {
		this.routerSectionNo5 = routerSectionNo5;
	}

	public String getRouterSectionNo6() {
		return routerSectionNo6;
	}

	public void setRouterSectionNo6(String routerSectionNo6) {
		this.routerSectionNo6 = routerSectionNo6;
	}

	public String getRouterSectionAreaNo1() {
		return routerSectionAreaNo1;
	}

	public void setRouterSectionAreaNo1(String routerSectionAreaNo1) {
		this.routerSectionAreaNo1 = routerSectionAreaNo1;
	}

	public String getRouterSectionAreaNo2() {
		return routerSectionAreaNo2;
	}

	public void setRouterSectionAreaNo2(String routerSectionAreaNo2) {
		this.routerSectionAreaNo2 = routerSectionAreaNo2;
	}

	public String getRouterSectionAreaNo3() {
		return routerSectionAreaNo3;
	}

	public void setRouterSectionAreaNo3(String routerSectionAreaNo3) {
		this.routerSectionAreaNo3 = routerSectionAreaNo3;
	}

	public String getRouterSectionAreaNo4() {
		return routerSectionAreaNo4;
	}

	public void setRouterSectionAreaNo4(String routerSectionAreaNo4) {
		this.routerSectionAreaNo4 = routerSectionAreaNo4;
	}

	public String getRouterSectionAreaNo5() {
		return routerSectionAreaNo5;
	}

	public void setRouterSectionAreaNo5(String routerSectionAreaNo5) {
		this.routerSectionAreaNo5 = routerSectionAreaNo5;
	}

	public String getRouterSectionAreaNo6() {
		return routerSectionAreaNo6;
	}

	public void setRouterSectionAreaNo6(String routerSectionAreaNo6) {
		this.routerSectionAreaNo6 = routerSectionAreaNo6;
	}

	public String getOriginalSectionAreaNo() {
		return originalSectionAreaNo;
	}

	public void setOriginalSectionAreaNo(String originalSectionAreaNo) {
		this.originalSectionAreaNo = originalSectionAreaNo;
	}

	public String getDestinationSectionAreaNo() {
		return destinationSectionAreaNo;
	}

	public void setDestinationSectionAreaNo(String destinationSectionAreaNo) {
		this.destinationSectionAreaNo = destinationSectionAreaNo;
	}

	public String getPrintSiteName() {
		return printSiteName;
	}

	public void setPrintSiteName(String printSiteName) {
		this.printSiteName = printSiteName;
	}

	public String getDestinationCrossCode() {
		return destinationCrossCode;
	}

	public void setDestinationCrossCode(String destinationCrossCode) {
		this.destinationCrossCode = destinationCrossCode;
	}

	public String getDestinationDmsName() {
		return destinationDmsName;
	}

	public void setDestinationDmsName(String destinationDmsName) {
		this.destinationDmsName = destinationDmsName;
	}

	public String getDestinationTabletrolleyCode() {
		return destinationTabletrolleyCode;
	}

	public void setDestinationTabletrolleyCode(String destinationTabletrolleyCode) {
		this.destinationTabletrolleyCode = destinationTabletrolleyCode;
	}

	public String getOriginalTabletrolleyCode() {
		return originalTabletrolleyCode;
	}

	public void setOriginalTabletrolleyCode(String originalTabletrolleyCode) {
		this.originalTabletrolleyCode = originalTabletrolleyCode;
	}

	public String getWaybillSignText() {
		return waybillSignText;
	}

	public void setWaybillSignText(String waybillSignText) {
		this.waybillSignText = waybillSignText;
	}

	public String getRoadCode() {
		return roadCode;
	}

	public void setRoadCode(String roadCode) {
		this.roadCode = roadCode;
	}

	/**
	 * @return the templatePaperSizeCode
	 */
	public String getTemplatePaperSizeCode() {
		return templatePaperSizeCode;
	}

	/**
	 * @param templatePaperSizeCode the templatePaperSizeCode to set
	 */
	public void setTemplatePaperSizeCode(String templatePaperSizeCode) {
		this.templatePaperSizeCode = templatePaperSizeCode;
	}

	/**
	 * @return the useNewTemplate
	 */
	public Boolean getUseNewTemplate() {
		return useNewTemplate;
	}

	/**
	 * @param useNewTemplate the useNewTemplate to set
	 */
	public void setUseNewTemplate(Boolean useNewTemplate) {
		this.useNewTemplate = useNewTemplate;
	}

	public String getCustomerPhoneText() {
		return customerPhoneText;
	}

	public void setCustomerPhoneText(String customerPhoneText) {
		this.customerPhoneText = customerPhoneText;
	}

	public String getRodeCode() {
		return rodeCode;
	}

	public void setRodeCode(String rodeCode) {
		this.rodeCode = rodeCode;
	}

	public String getConsignerPrefixText() {
		return consignerPrefixText;
	}

	public void setConsignerPrefixText(String consignerPrefixText) {
		this.consignerPrefixText = consignerPrefixText;
	}

	public String getConsignerTelText() {
		return consignerTelText;
	}

	public void setConsignerTelText(String consignerTelText) {
		this.consignerTelText = consignerTelText;
	}

	public Integer getWillPrintPackageIndex() {
		return willPrintPackageIndex;
	}

	public void setWillPrintPackageIndex(Integer willPrintPackageIndex) {
		this.willPrintPackageIndex = willPrintPackageIndex;
	}

	public String getOldWaybillCode() {
		return oldWaybillCode;
	}

	public void setOldWaybillCode(String oldWaybillCode) {
		this.oldWaybillCode = oldWaybillCode;
	}

	public String getSpecialMark1() {
		return specialMark1;
	}

	public void setSpecialMark1(String specialMark1) {
		this.specialMark1 = specialMark1;
		specialMark1Builder = new StringBuilder(specialMark1);
	}

	/**
	 * 特殊标记字段追加标记，不包含时加入标记
	 * @param markText
	 */
	public void appendSpecialMark1(String markText){
		//标识不为空，并且不包含此标记时加入标记
		if(markText!=null && markText.length()>0
				&& specialMark1Builder.indexOf(markText) < 0){
			if(specialMark1.length()>0){
				specialMark1Builder.append(" ");
			}
			specialMark1Builder.append(markText);
			this.specialMark1 = specialMark1Builder.toString();
		}
	}

	/**
	 * @return the templateGroupCode
	 */
	public String getTemplateGroupCode() {
		return templateGroupCode;
	}

	/**
	 * @param templateGroupCode the templateGroupCode to set
	 */
	public void setTemplateGroupCode(String templateGroupCode) {
		this.templateGroupCode = templateGroupCode;
	}

	/**
	 * @return the destinationCityDmsCode
	 */
	public String getDestinationCityDmsCode() {
		return destinationCityDmsCode;
	}

	/**
	 * @param destinationCityDmsCode the destinationCityDmsCode to set
	 */
	public void setDestinationCityDmsCode(String destinationCityDmsCode) {
		this.destinationCityDmsCode = destinationCityDmsCode;
	}

	/**
	 * @return the prepareSiteCode
	 */
	public Integer getPrepareSiteCode() {
		return prepareSiteCode;
	}

	/**
	 * @param prepareSiteCode the prepareSiteCode to set
	 */
	public void setPrepareSiteCode(Integer prepareSiteCode) {
		this.prepareSiteCode = prepareSiteCode;
	}

	/**
	 * @return the prepareSiteName
	 */
	public String getPrepareSiteName() {
		return prepareSiteName;
	}

	/**
	 * @param prepareSiteName the prepareSiteName to set
	 */
	public void setPrepareSiteName(String prepareSiteName) {
		this.prepareSiteName = prepareSiteName;
	}

	/**
	 * @return the purposefulDmsCode
	 */
	public Integer getPurposefulDmsCode() {
		return purposefulDmsCode;
	}

	/**
	 * @param purposefulDmsCode the purposefulDmsCode to set
	 */
	public void setPurposefulDmsCode(Integer purposefulDmsCode) {
		this.purposefulDmsCode = purposefulDmsCode;
	}

	/**
	 * @return the purposefulDmsName
	 */
	public String getPurposefulDmsName() {
		return purposefulDmsName;
	}

	/**
	 * @param purposefulDmsName the purposefulDmsName to set
	 */
	public void setPurposefulDmsName(String purposefulDmsName) {
		this.purposefulDmsName = purposefulDmsName;
	}

	/**
	 * @return the originalCrossCode
	 */
	public String getOriginalCrossCode() {
		return originalCrossCode;
	}

	/**
	 * @param originalCrossCode the originalCrossCode to set
	 */
	public void setOriginalCrossCode(String originalCrossCode) {
		this.originalCrossCode = originalCrossCode;
	}

	/**
	 * @return the purposefulCrossCode
	 */
	public String getPurposefulCrossCode() {
		return purposefulCrossCode;
	}

	/**
	 * @param purposefulCrossCode the purposefulCrossCode to set
	 */
	public void setPurposefulCrossCode(String purposefulCrossCode) {
		this.purposefulCrossCode = purposefulCrossCode;
	}

	/**
	 * @return the originalTabletrolley
	 */
	public String getOriginalTabletrolley() {
		return originalTabletrolley;
	}

	/**
	 * @param originalTabletrolley the originalTabletrolley to set
	 */
	public void setOriginalTabletrolley(String originalTabletrolley) {
		this.originalTabletrolley = originalTabletrolley;
	}

	/**
	 * @return the purposefulTableTrolley
	 */
	public String getPurposefulTableTrolley() {
		return purposefulTableTrolley;
	}

	/**
	 * @param purposefulTableTrolley the purposefulTableTrolley to set
	 */
	public void setPurposefulTableTrolley(String purposefulTableTrolley) {
		this.purposefulTableTrolley = purposefulTableTrolley;
	}
	/**
	 * @return the labelFileType
	 */
	public Integer getLabelFileType() {
		return labelFileType;
	}
	/**
	 * @param labelFileType the labelFileType to set
	 */
	public void setLabelFileType(Integer labelFileType) {
		this.labelFileType = labelFileType;
	}
	/**
	 * @return the labelFileUrl
	 */
	public String getLabelFileUrl() {
		return labelFileUrl;
	}
	/**
	 * @param labelFileUrl the labelFileUrl to set
	 */
	public void setLabelFileUrl(String labelFileUrl) {
		this.labelFileUrl = labelFileUrl;
	}
	/**
	 * @return the templateVersionStr
	 */
	public String getTemplateVersionStr() {
		return templateVersionStr;
	}
	/**
	 * @param templateVersionStr the templateVersionStr to set
	 */
	public void setTemplateVersionStr(String templateVersionStr) {
		this.templateVersionStr = templateVersionStr;
	}
	/**
	 * @return the promiseText
	 */
	public String getPromiseText() {
		return promiseText;
	}
	/**
	 * @param promiseText the promiseText to set
	 */
	public void setPromiseText(String promiseText) {
		this.promiseText = promiseText;
	}
	public String getPromiseTextC() {
		return promiseTextC;
	}
	public void setPromiseTextC(String promiseTextC) {
		this.promiseTextC = promiseTextC;
	}
	/**
	 * @return the timeCategory
	 */
	public String getTimeCategory() {
		return timeCategory;
	}
	/**
	 * @param timeCategory the timeCategory to set
	 */
	public void setTimeCategory(String timeCategory) {
		this.timeCategory = timeCategory;
	}
	/**
	 * @return the orderCode
	 */
	public String getOrderCode() {
		return orderCode;
	}
	/**
	 * @param orderCode the orderCode to set
	 */
	public void setOrderCode(String orderCode) {
		this.orderCode = orderCode;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public String getBackupRoadCode() {
		return backupRoadCode;
	}

	public void setBackupRoadCode(String backupRoadCode) {
		this.backupRoadCode = backupRoadCode;
	}

	public String getBcSign() {
		return bcSign;
	}

	public String getPopularizeMatrixCodeDesc() {
		return popularizeMatrixCodeDesc;
	}

	public void setPopularizeMatrixCodeDesc(String popularizeMatrixCodeDesc) {
		this.popularizeMatrixCodeDesc = popularizeMatrixCodeDesc;
	}

	public void setBcSign(String bcSign) {
		this.bcSign = bcSign;
	}

	public String getWaybillVasSign() {
		return waybillVasSign;
	}

	public void setWaybillVasSign(String waybillVasSign) {
		this.waybillVasSign = waybillVasSign;
	}

	/**
	 * @return the printAddressRemark
	 */
	public String getPrintAddressRemark() {
		return printAddressRemark;
	}
	/**
	 * @param printAddressRemark the printAddressRemark to set
	 */
	public void setPrintAddressRemark(String printAddressRemark) {
		this.printAddressRemark = printAddressRemark;
	}
	public String getSpecialMarkNew() {
		return specialMarkNew;
	}
	public void setSpecialMarkNew(String specialMarkNew) {
		this.specialMarkNew = specialMarkNew;
		if(specialMarkNew != null){
			specialMarkNewBuilder = new StringBuilder(specialMarkNew);
		}else{
			specialMarkNewBuilder = new StringBuilder();
		}
	}
	public String getCollectionAddress() {
		return collectionAddress;
	}
	public void setCollectionAddress(String collectionAddress) {
		this.collectionAddress = collectionAddress;
	}
	public String getTransportTypeText() {
		return transportTypeText;
	}
	public void setTransportTypeText(String transportTypeText) {
		this.transportTypeText = transportTypeText;
	}
	public String getRespectTypeText() {
		return respectTypeText;
	}
	public void setRespectTypeText(String respectTypeText) {
		this.respectTypeText = respectTypeText;
	}
	public String getCodMoneyText() {
		return codMoneyText;
	}
	public void setCodMoneyText(String codMoneyText) {
		this.codMoneyText = codMoneyText;
	}
	public String getTotalChargeText() {
		return totalChargeText;
	}
	public void setTotalChargeText(String totalChargeText) {
		this.totalChargeText = totalChargeText;
	}
	public String getTransportModeFlag() {
		return transportModeFlag;
	}
	public void setTransportModeFlag(String transportModeFlag) {
		this.transportModeFlag = transportModeFlag;
	}

    public String getWaterMark() {
        return waterMark;
    }

    public void setWaterMark(String waterMark) {
        this.waterMark = waterMark;
    }

	public Integer getOutputType() {
		return outputType;
	}

	public void setOutputType(Integer outputType) {
		this.outputType = outputType;
	}
	public String getAoiCode() {
		return aoiCode;
	}
	public void setAoiCode(String aoiCode) {
		this.aoiCode = aoiCode;
	}
	public Integer getLocalSchedule() {
		return localSchedule;
	}
	public void setLocalSchedule(Integer localSchedule) {
		this.localSchedule = localSchedule;
	}

	public Integer getTempOriginalCrossType() {
		return tempOriginalCrossType;
	}

	public void setTempOriginalCrossType(Integer tempOriginalCrossType) {
		this.tempOriginalCrossType = tempOriginalCrossType;
	}

	public String getSpliceGoodsNameWithTitle() {
		return spliceGoodsNameWithTitle;
	}

	public void setSpliceGoodsNameWithTitle(String spliceGoodsNameWithTitle) {
		this.spliceGoodsNameWithTitle = spliceGoodsNameWithTitle;
	}

	public String getVerificationCodeWithTitle() {
		return verificationCodeWithTitle;
	}

	public void setVerificationCodeWithTitle(String verificationCodeWithTitle) {
		this.verificationCodeWithTitle = verificationCodeWithTitle;
	}
	public String getLabelFileDownloadUrl() {
		return labelFileDownloadUrl;
	}
	public void setLabelFileDownloadUrl(String labelFileDownloadUrl) {
		this.labelFileDownloadUrl = labelFileDownloadUrl;
	}

	public String getTemplateSysName() {
		return templateSysName;
	}

	public void setTemplateSysName(String templateSysName) {
		this.templateSysName = templateSysName;
	}
}
