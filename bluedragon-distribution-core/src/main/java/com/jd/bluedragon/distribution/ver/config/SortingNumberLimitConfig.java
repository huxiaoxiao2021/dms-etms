package com.jd.bluedragon.distribution.ver.config;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
	private Map<Integer, Set<Integer>> siteTypes;

	public Map<Integer, Set<Integer>> getSiteTypes() {
		return siteTypes;
	}

	public void setSiteTypes(Map<Integer, Set<Integer>> siteTypes) {
		this.siteTypes = siteTypes;
	}
}
