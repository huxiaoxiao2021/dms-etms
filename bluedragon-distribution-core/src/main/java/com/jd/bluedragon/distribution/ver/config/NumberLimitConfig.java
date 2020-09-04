package com.jd.bluedragon.distribution.ver.config;

import java.io.Serializable;

/**
 * 
 * @ClassName: PackageNumLimitConfig
 * @Description: 数量限制配置
 * @author: wuyoude
 * @date: 2019年10月17日 上午9:53:17
 *
 */
public class NumberLimitConfig implements Serializable {
	private static final long serialVersionUID = 1L;
	private Boolean isOpen = Boolean.FALSE;
	private Integer maxNum = 20000;
	/**
	 * @return the isOpen
	 */
	public Boolean getIsOpen() {
		return isOpen;
	}
	/**
	 * @param isOpen the isOpen to set
	 */
	public void setIsOpen(Boolean isOpen) {
		this.isOpen = isOpen;
	}
	/**
	 * @return the maxNum
	 */
	public Integer getMaxNum() {
		return maxNum;
	}
	/**
	 * @param maxNum the maxNum to set
	 */
	public void setMaxNum(Integer maxNum) {
		this.maxNum = maxNum;
	}
}