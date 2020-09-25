package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: LoginCheckConfig
 * @Description: 登录检查配置
 * @author: wuyoude
 * @date: 2018年5月15日 下午8:50:40
 *
 */
public class LoginCheckConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 默认检查总开关
     */
    private Boolean masterSwitch;
    /**
     * 需要检查的客户端类型列表
     */
    private List<Integer> programTypes;
    /**
     * 需要检查的客户端的机构列表
     */
    private List<Integer> orgCodes;
    /**
     * 需要检查的客户端的站点列表
     */
    private List<Integer> siteCodes;
    /**
     * 基础版本信息
     */
    private List<String> baseVersionCodes;    
	/**
	 * @return the masterSwitch
	 */
	public Boolean getMasterSwitch() {
		return masterSwitch;
	}
	/**
	 * @param masterSwitch the masterSwitch to set
	 */
	public void setMasterSwitch(Boolean masterSwitch) {
		this.masterSwitch = masterSwitch;
	}
	/**
	 * @return the programTypes
	 */
	public List<Integer> getProgramTypes() {
		return programTypes;
	}
	/**
	 * @param programTypes the programTypes to set
	 */
	public void setProgramTypes(List<Integer> programTypes) {
		this.programTypes = programTypes;
	}
	/**
	 * @return the orgCodes
	 */
	public List<Integer> getOrgCodes() {
		return orgCodes;
	}
	/**
	 * @param orgCodes the orgCodes to set
	 */
	public void setOrgCodes(List<Integer> orgCodes) {
		this.orgCodes = orgCodes;
	}
	/**
	 * @return the siteCodes
	 */
	public List<Integer> getSiteCodes() {
		return siteCodes;
	}
	/**
	 * @param siteCodes the siteCodes to set
	 */
	public void setSiteCodes(List<Integer> siteCodes) {
		this.siteCodes = siteCodes;
	}
	public List<String> getBaseVersionCodes() {
		return baseVersionCodes;
	}
	public void setBaseVersionCodes(List<String> baseVersionCodes) {
		this.baseVersionCodes = baseVersionCodes;
	}

}
