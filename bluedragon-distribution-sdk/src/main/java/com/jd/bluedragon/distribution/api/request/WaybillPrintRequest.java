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
	 * 扫描条码
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
	 * 包裹称重类型 
	 */
	private Integer weightOperType;
	/**
	 * 包裹称重信息
	 */
	private WeightOperFlow weightOperFlow;
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
}
