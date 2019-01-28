package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;

/**
 * 
 * @ClassName: WaybillPrintRequest
 * @Description: 包裹标签打印请求实体
 * @author: wuyoude
 * @date: 2018年1月23日 下午10:36:18
 *
 */
public class WaybillPrintRequest extends JdRequest{
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
     *  应用程序类型-40-青龙打印客户端
     */
    private Integer programType;
    /**
	 * 客户端版本号:20180104WM
     */
    private String versionCode;
	/**
	 * 操作类型-区分打印类型
	 */
	private Integer operateType;
	/**
	 * 当前操作的分拣中心编码
	 */
	private Integer dmsSiteCode;
	/**
	 * 包裹号/运单号
	 */
	private String barCode;
	/**
	 * 目的站点-默认值为0-以预分拣站点为准，非0则为重新调度站点
	 */
	private Integer targetSiteCode = 0;
	/**
	 * 无纸化标识
	 */
	private Boolean nopaperFlg = false;
	/**
	 * 信任商家标识
	 * */
	private Boolean trustBusinessFlag = false;
	/**
	 * 包裹称重类型 
	 */
	private Integer weightOperType;

    /**
     * 始发站点类型（temp）
     * */
    private Integer startSiteType;
    /**
     * 是否获取称重信息（temp）
     */
    private Integer packOpeFlowFlg;
	/**
	 * 包裹称重信息
	 */
	private WeightOperFlow weightOperFlow;


	/** 包裹标签模板名称 **/
	private String templateName;

	/** 包裹标签模板版本 **/
	private Integer templateVersion;

	/**
	 * 操作人编码
	 */
	private Integer userCode;

	/**
	 * 操作人姓名
	 */
	private String userName;

	/**
	 * 操作人所属站点编号
	 */
	private Integer siteCode;

	/**
	 * 操作人所属站点名称
	 */
	private String siteName;



	public Boolean getTrustBusinessFlag() {
		return trustBusinessFlag;
	}

	public void setTrustBusinessFlag(Boolean trustBusinessFlag) {
		this.trustBusinessFlag = trustBusinessFlag;
	}

	/**
	 * @return the programType
	 */
	public Integer getProgramType() {
		return programType;
	}
	/**
	 * @param programType the programType to set
	 */
	public void setProgramType(Integer programType) {
		this.programType = programType;
	}
	/**
	 * @return the versionCode
	 */
	public String getVersionCode() {
		return versionCode;
	}
	/**
	 * @param versionCode the versionCode to set
	 */
	public void setVersionCode(String versionCode) {
		this.versionCode = versionCode;
	}
	/**
	 * @return the operateType
	 */
	public Integer getOperateType() {
		return operateType;
	}
	/**
	 * @param operateType the operateType to set
	 */
	public void setOperateType(Integer operateType) {
		this.operateType = operateType;
	}
	/**
	 * @return the dmsSiteCode
	 */
	public Integer getDmsSiteCode() {
		return dmsSiteCode;
	}
	/**
	 * @param dmsSiteCode the dmsSiteCode to set
	 */
	public void setDmsSiteCode(Integer dmsSiteCode) {
		this.dmsSiteCode = dmsSiteCode;
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
	/**
	 * @return the targetSiteCode
	 */
	public Integer getTargetSiteCode() {
		return targetSiteCode;
	}
	/**
	 * @param targetSiteCode the targetSiteCode to set
	 */
	public void setTargetSiteCode(Integer targetSiteCode) {
		this.targetSiteCode = targetSiteCode;
	}
	/**
	 * @return the nopaperFlg
	 */
	public Boolean getNopaperFlg() {
		return nopaperFlg;
	}
	/**
	 * @param nopaperFlg the nopaperFlg to set
	 */
	public void setNopaperFlg(Boolean nopaperFlg) {
		this.nopaperFlg = nopaperFlg;
	}
	/**
	 * @return the weightOperType
	 */
	public Integer getWeightOperType() {
		return weightOperType;
	}
	/**
	 * @param weightOperType the weightOperType to set
	 */
	public void setWeightOperType(Integer weightOperType) {
		this.weightOperType = weightOperType;
	}

    public Integer getPackOpeFlowFlg() {
        return packOpeFlowFlg;
    }

    public void setPackOpeFlowFlg(Integer packOpeFlowFlg) {
        this.packOpeFlowFlg = packOpeFlowFlg;
    }

    public Integer getStartSiteType() {
        return startSiteType;
    }

    public void setStartSiteType(Integer startSiteType) {
        this.startSiteType = startSiteType;
    }

    /**
	 * @return the weightOperFlow
	 */
	public WeightOperFlow getWeightOperFlow() {
		return weightOperFlow;
	}
	/**
	 * @param weightOperFlow the weightOperFlow to set
	 */
	public void setWeightOperFlow(WeightOperFlow weightOperFlow) {
		this.weightOperFlow = weightOperFlow;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public Integer getTemplateVersion() {
		return templateVersion;
	}

	public void setTemplateVersion(Integer templateVersion) {
		this.templateVersion = templateVersion;
	}

	@Override
	public Integer getUserCode() {
		return userCode;
	}

	@Override
	public void setUserCode(Integer userCode) {
		this.userCode = userCode;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public Integer getSiteCode() {
		return siteCode;
	}

	@Override
	public void setSiteCode(Integer siteCode) {
		this.siteCode = siteCode;
	}

	@Override
	public String getSiteName() {
		return siteName;
	}

	@Override
	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}
}
