package com.jd.bluedragon.distribution.api.request;

import com.jd.bluedragon.distribution.api.JdRequest;
import com.jd.bluedragon.distribution.api.domain.WeightOperFlow;
import com.jd.bluedragon.distribution.print.domain.SurfaceOutputTypeEnum;

import java.util.Date;

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
	 * 默认dpi-203
	 */
	private static Integer DPI_DEFAULT = 203;
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
	 * 青龙业主号
	 */
	private String customerCode;
	/**
	 * 包裹号/运单号
	 */
	private String barCode;
	/**
	 * 目的站点-默认值为0-以预分拣站点为准，非0则为重新调度站点
	 */
	private Integer targetSiteCode = 0;

	/**
	 * 面单输出方式
	 * @see SurfaceOutputTypeEnum
	 * 	0、打印
	 * 	1、预览
	 */
	private Integer outputType = 0;
	/**
	 * 无纸化标识
	 */
	private Boolean nopaperFlg;
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
     * 是否获取称重信息（temp）0不称重 1称重
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
     * 横向分辨率
     */
    private Integer dpiX = DPI_DEFAULT;
    /**
     * 纵向分辨率
     */
    private Integer dpiY = DPI_DEFAULT;
    /**
     * 纸张尺寸编码
     */
    private String paperSizeCode;

	/**
	 * 是否启用称重量方
	 * 0不启用
	 * 1启用称重
	 * 2启用量方
	 * 3启用称重量方
     */
	private Integer weightVolumeOperEnable;

	/**
	 * 包裹的index 第几件包裹 默认为-1
     */
	private Integer packageIndex = -1;

	/**
	 * 旧单号
     */
	private String oldBarCode;

    /**
     * 是否取消鸡毛信服务；
     * 根据waybillsign确认是鸡毛信运单 才有用
     * true 取消，false 不取消
     */
	private boolean cancelFeatherLetter;

    /**
     * 鸡毛信设备号
     * 根据waybillsign确认是鸡毛信运单 才有用
     */
	private String featherLetterDeviceNo;

	/**
	 * 自动识别包裹标签打印标识
	 * */
	private Boolean discernFlag = false;
    /**
     * 商家Id
     */
	private Integer businessId;
    /**
     * 扫描单号类
     */
	private Integer barCodeType;

	/**
	 * 靑流箱号 驻厂打印绑定靑流箱用
	 */
	private String reBoxCode;

	/**
	 * 用户ERP
	 */
	private String userERP;

    /**
     * 包裹号或运单号，目前只有包裹补打使用
     */
    private String packageBarCode;

	/**
	 * 请求时间
	 */
	private Date requestTime = new Date();
	
	/**
	 * 返调度标识 1-返调度 0-其他打印
	 */
	private Integer localSchedule;
	
	/**
	 * 启用新oss标识
	 */
	private Boolean useAmazon;

	public Boolean getTrustBusinessFlag() {
		return trustBusinessFlag;
	}

	public void setTrustBusinessFlag(Boolean trustBusinessFlag) {
		this.trustBusinessFlag = trustBusinessFlag;
	}

	public String getReBoxCode() {
		return reBoxCode;
	}

	public void setReBoxCode(String reBoxCode) {
		this.reBoxCode = reBoxCode;
	}

	public String getUserERP() {
		return userERP;
	}

	public void setUserERP(String userERP) {
		this.userERP = userERP;
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
	 * @return the customerCode
	 */
	public String getCustomerCode() {
		return customerCode;
	}
	/**
	 * @param customerCode the customerCode to set
	 */
	public void setCustomerCode(String customerCode) {
		this.customerCode = customerCode;
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
	/**
	 * 判断是否已称重：weightOperFlow不为空，weightOperFlow.weight > 0
	 */
	public boolean hasWeighted() {
		return this.weightOperFlow != null && this.weightOperFlow.getWeight() > 0;
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

	/**
	 * @return the dpiX
	 */
	public Integer getDpiX() {
		return dpiX;
	}

	/**
	 * @param dpiX the dpiX to set
	 */
	public void setDpiX(Integer dpiX) {
		this.dpiX = dpiX;
	}

	/**
	 * @return the dpiY
	 */
	public Integer getDpiY() {
		return dpiY;
	}

	/**
	 * @param dpiY the dpiY to set
	 */
	public void setDpiY(Integer dpiY) {
		this.dpiY = dpiY;
	}

	/**
	 * @return the paperSizeCode
	 */
	public String getPaperSizeCode() {
		return paperSizeCode;
	}

	/**
	 * @param paperSizeCode the paperSizeCode to set
	 */
	public void setPaperSizeCode(String paperSizeCode) {
		this.paperSizeCode = paperSizeCode;
	}

	public Integer getWeightVolumeOperEnable() {
		return weightVolumeOperEnable;
	}

	public void setWeightVolumeOperEnable(Integer weightVolumeOperEnable) {
		this.weightVolumeOperEnable = weightVolumeOperEnable;
	}

	public Integer getPackageIndex() {
		return packageIndex;
	}

	public void setPackageIndex(Integer packageIndex) {
		this.packageIndex = packageIndex;
	}

	public String getOldBarCode() {
		return oldBarCode;
	}

	public void setOldBarCode(String oldBarCode) {
		this.oldBarCode = oldBarCode;
	}

    public boolean isCancelFeatherLetter() {
        return cancelFeatherLetter;
    }

    public void setCancelFeatherLetter(boolean cancelFeatherLetter) {
        this.cancelFeatherLetter = cancelFeatherLetter;
    }

    public String getFeatherLetterDeviceNo() {
        return featherLetterDeviceNo;
    }

    public void setFeatherLetterDeviceNo(String featherLetterDeviceNo) {
        this.featherLetterDeviceNo = featherLetterDeviceNo;
    }

	public Boolean getDiscernFlag() {
		return discernFlag;
	}

	public void setDiscernFlag(Boolean discernFlag) {
		this.discernFlag = discernFlag;
	}

	public Integer getBusinessId() {
		return businessId;
	}

	public void setBusinessId(Integer businessId) {
		this.businessId = businessId;
	}

	public Integer getBarCodeType() {
		return barCodeType;
	}

	public void setBarCodeType(Integer barCodeType) {
		this.barCodeType = barCodeType;
	}

    public String getPackageBarCode() {
        return packageBarCode;
    }

    public void setPackageBarCode(String packageBarCode) {
        this.packageBarCode = packageBarCode;
    }

	public Date getRequestTime() {
		return requestTime;
	}

	public void setRequestTime(Date requestTime) {
		this.requestTime = requestTime;
	}

	public Integer getOutputType() {
		return outputType;
	}

	public void setOutputType(Integer outputType) {
		this.outputType = outputType;
	}

	public Integer getLocalSchedule() {
		return localSchedule;
	}

	public void setLocalSchedule(Integer localSchedule) {
		this.localSchedule = localSchedule;
	}

	public Boolean getUseAmazon() {
		return useAmazon;
	}

	public void setUseAmazon(Boolean useAmazon) {
		this.useAmazon = useAmazon;
	}
}
