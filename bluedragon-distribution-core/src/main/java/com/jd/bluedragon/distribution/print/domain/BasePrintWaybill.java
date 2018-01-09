package com.jd.bluedragon.distribution.print.domain;

import java.io.Serializable;

import com.jd.bluedragon.utils.StringHelper;

/**
 * 面单打印信息
 * @ClassName: BasePrintWaybill
 * @Description: TODO
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
    private String specialMark ;

    /**
    * 包裹特殊标识-builder类型
    */
    private StringBuilder specialMarkBuilder = new StringBuilder();
    /**
     * 收件公司名称
     */
    private String consigneeCompany;
    /**
     * 运输产品类型
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
    	if(StringHelper.isNotEmpty(markText)
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
    	if(StringHelper.isNotEmpty(markText)
    			&& StringHelper.isNotEmpty(markText1)
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

}
