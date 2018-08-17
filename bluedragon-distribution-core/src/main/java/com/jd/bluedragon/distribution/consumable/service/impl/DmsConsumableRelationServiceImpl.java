package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.distribution.packingconsumable.domain.PackingConsumableBaseInfo;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.dao.DmsConsumableRelationDao;
import com.jd.bluedragon.distribution.consumable.service.DmsConsumableRelationService;

import java.util.List;

/**
 *
 * @ClassName: DmsConsumableRelationServiceImpl
 * @Description: 分拣中心耗材关系表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("dmsConsumableRelationService")
public class DmsConsumableRelationServiceImpl extends BaseService<DmsConsumableRelation> implements DmsConsumableRelationService {

	@Autowired
	@Qualifier("dmsConsumableRelationDao")
	private DmsConsumableRelationDao dmsConsumableRelationDao;

	@Override
	public Dao<DmsConsumableRelation> getDao() {
		return this.dmsConsumableRelationDao;
	}

	@Override
	public List<PackingConsumableBaseInfo> getPackingConsumableInfoByDmsId(Integer dmsId) {
		return dmsConsumableRelationDao.getPackingConsumableInfoByDmsId(dmsId);
	}
}
