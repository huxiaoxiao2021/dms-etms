package com.jd.bluedragon.distribution.kuguan.service;

import java.util.List;
import java.util.Map;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;

@Deprecated
public interface KuGuanService {
	@Deprecated
	KuGuanDomain queryByParams(Map<String, Object> paramMap);
	@Deprecated
	public List<KuGuanDomain> queryMingxi(String kdanhao) ;
}
