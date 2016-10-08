package com.jd.bluedragon.distribution.base.service;

import java.util.List;

public interface KvIndexService {

	int add(String keyword, String value);

	public List<Integer> queryCreateSiteCodesByKey(String keyword);

}
