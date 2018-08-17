package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.consumable.domain.PackingConsumableInfo;
import com.jd.bluedragon.distribution.consumable.dao.PackingConsumableInfoDao;
import com.jd.bluedragon.distribution.consumable.service.PackingConsumableInfoService;

/**
 *
 * @ClassName: PackingConsumableInfoServiceImpl
 * @Description: 包装耗材信息表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("packingConsumableInfoService")
public class PackingConsumableInfoServiceImpl extends BaseService<PackingConsumableInfo> implements PackingConsumableInfoService {

	@Autowired
	@Qualifier("packingConsumableInfoDao")
	private PackingConsumableInfoDao packingConsumableInfoDao;

	@Override
	public Dao<PackingConsumableInfo> getDao() {
		return this.packingConsumableInfoDao;
	}

}
