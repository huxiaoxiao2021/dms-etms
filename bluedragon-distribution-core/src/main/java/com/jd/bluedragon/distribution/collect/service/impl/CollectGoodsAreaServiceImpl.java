package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsArea;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsAreaDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsAreaService;

/**
 *
 * @ClassName: CollectGoodsAreaServiceImpl
 * @Description: 集货区表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsAreaService")
public class CollectGoodsAreaServiceImpl extends BaseService<CollectGoodsArea> implements CollectGoodsAreaService {

	@Autowired
	@Qualifier("collectGoodsAreaDao")
	private CollectGoodsAreaDao collectGoodsAreaDao;

	@Override
	public Dao<CollectGoodsArea> getDao() {
		return this.collectGoodsAreaDao;
	}

}
