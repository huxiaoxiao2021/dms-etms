package com.jd.bluedragon.distribution.base.domain;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * @ClassName: JsfVerifyConfig
 * @Description: jsf权限配置
 * @author: wuyoude
 * @date: 2019年3月20日 下午5:29:04
 *
 */
public class JsfVerifyConfig implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * 系统标识
     */
    private String systemCode;
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 允许访问所有标识
     */
    private Boolean allowAll = Boolean.FALSE;
    /**
     * 允许访问的业务类型集合
     */
    private List<Integer> businessTypes;
    /**
     * 允许访问的业务类型-操作类型集合
     */
    private List<Integer> operateTypes;
	/**
	 * @return the systemCode
	 */
	public String getSystemCode() {
		return systemCode;
	}
	/**
	 * @param systemCode the systemCode to set
	 */
	public void setSystemCode(String systemCode) {
		this.systemCode = systemCode;
	}
	/**
	 * @return the secretKey
	 */
	public String getSecretKey() {
		return secretKey;
	}
	/**
	 * @param secretKey the secretKey to set
	 */
	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}
	/**
	 * @return the allowAll
	 */
	public Boolean getAllowAll() {
		return allowAll;
	}
	/**
	 * @param allowAll the allowAll to set
	 */
	public void setAllowAll(Boolean allowAll) {
		this.allowAll = allowAll;
	}
	/**
	 * @return the businessTypes
	 */
	public List<Integer> getBusinessTypes() {
		return businessTypes;
	}
	/**
	 * @param businessTypes the businessTypes to set
	 */
	public void setBusinessTypes(List<Integer> businessTypes) {
		this.businessTypes = businessTypes;
	}
	/**
	 * @return the operateTypes
	 */
	public List<Integer> getOperateTypes() {
		return operateTypes;
	}
	/**
	 * @param operateTypes the operateTypes to set
	 */
	public void setOperateTypes(List<Integer> operateTypes) {
		this.operateTypes = operateTypes;
	}
}
