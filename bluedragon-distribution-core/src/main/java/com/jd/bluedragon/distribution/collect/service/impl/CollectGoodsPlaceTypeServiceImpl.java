package com.jd.bluedragon.distribution.collect.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.collect.domain.CollectGoodsPlaceType;
import com.jd.bluedragon.distribution.collect.dao.CollectGoodsPlaceTypeDao;
import com.jd.bluedragon.distribution.collect.service.CollectGoodsPlaceTypeService;

/**
 *
 * @ClassName: CollectGoodsPlaceTypeServiceImpl
 * @Description: 集货位类型表--Service接口实现
 * @author wuyoude
 * @date 2019年08月14日 13:56:21
 *
 */
@Service("collectGoodsPlaceTypeService")
public class CollectGoodsPlaceTypeServiceImpl extends BaseService<CollectGoodsPlaceType> implements CollectGoodsPlaceTypeService {

	@Autowired
	@Qualifier("collectGoodsPlaceTypeDao")
	private CollectGoodsPlaceTypeDao collectGoodsPlaceTypeDao;

	@Override
	public Dao<CollectGoodsPlaceType> getDao() {
		return this.collectGoodsPlaceTypeDao;
	}

}
