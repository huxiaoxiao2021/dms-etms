package com.jd.bluedragon.distribution.ver.config;

import java.io.Serializable;
import java.util.List;

/**
 * 站点分拣数量限制配置
 * @author wuyoude
 *
 */
public class SortingNumberLimitConfig implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * 需要限制的站点类型
	 */
	private List<Integer> siteTypes;
	
	public List<Integer> getSiteTypes() {
		return siteTypes;
	}
	public void setSiteTypes(List<Integer> siteTypes) {
		this.siteTypes = siteTypes;
	}
	
}
