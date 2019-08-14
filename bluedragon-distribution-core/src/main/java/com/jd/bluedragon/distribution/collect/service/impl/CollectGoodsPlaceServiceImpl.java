package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlace;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceService;

/**
 *
 * @ClassName: CollectGoodsPlaceServiceImpl
 * @Description: 集货位表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsPlaceService")
public class CollectGoodsPlaceServiceImpl extends BaseService<CollectGoodsPlace> implements CollectGoodsPlaceService {

	@Autowired
	@Qualifier("collectGoodsPlaceDao")
	private CollectGoodsPlaceDao collectGoodsPlaceDao;

	@Override
	public Dao<CollectGoodsPlace> getDao() {
		return this.collectGoodsPlaceDao;
	}

}
