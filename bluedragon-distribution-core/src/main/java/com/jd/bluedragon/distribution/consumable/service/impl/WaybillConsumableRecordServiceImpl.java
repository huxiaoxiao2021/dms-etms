package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.BaseService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRecordService;

/**
 *
 * @ClassName: WaybillConsumableRecordServiceImpl
 * @Description: 运单耗材记录表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("waybillConsumableRecordService")
public class WaybillConsumableRecordServiceImpl extends BaseService<WaybillConsumableRecord> implements WaybillConsumableRecordService {

	@Autowired
	@Qualifier("waybillConsumableRecordDao")
	private WaybillConsumableRecordDao waybillConsumableRecordDao;

	@Override
	public Dao<WaybillConsumableRecord> getDao() {
		return this.waybillConsumableRecordDao;
	}

}
