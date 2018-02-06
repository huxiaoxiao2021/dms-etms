package com.jd.bluedragon.distribution.base.service;

import java.util.List;

public interface KvIndexService {

	int add(String keyword, String value);

	public List<Integer> queryCreateSiteCodesByKey(String keyword);
	/**
	 * 查询包裹/箱号最近的操作站点，按操作时间降序排列并去重
	 * @param keyword
	 * @return
	 */
	List<Integer> queryRecentSiteCodesByKey(String keyword);
}
