package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsDetail;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsDetailDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsDetailService;

/**
 *
 * @ClassName: CollectGoodsDetailServiceImpl
 * @Description: --Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsDetailService")
public class CollectGoodsDetailServiceImpl extends BaseService<CollectGoodsDetail> implements CollectGoodsDetailService {

	@Autowired
	@Qualifier("collectGoodsDetailDao")
	private CollectGoodsDetailDao collectGoodsDetailDao;

	@Override
	public Dao<CollectGoodsDetail> getDao() {
		return this.collectGoodsDetailDao;
	}

}
