package com.jd.bluedragon.distribution.kuguan.service;

import java.util.List;
import java.util.Map;
import com.jd.bluedragon.distribution.kuguan.domain.KuGuanDomain;

public interface KuGuanService {
	KuGuanDomain queryByParams(Map<String, Object> paramMap);
	public List<KuGuanDomain> queryMingxi(String kdanhao) ;
}
