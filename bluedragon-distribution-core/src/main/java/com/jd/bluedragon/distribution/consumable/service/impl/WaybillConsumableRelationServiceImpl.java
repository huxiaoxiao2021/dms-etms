package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRelationDao;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRelationServiceImpl
 * @Description: 运单耗材关系表--Service接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Service("waybillConsumableRelationService")
public class WaybillConsumableRelationServiceImpl extends BaseService<WaybillConsumableRelation> implements WaybillConsumableRelationService {

	@Autowired
	@Qualifier("waybillConsumableRelationDao")
	private WaybillConsumableRelationDao waybillConsumableRelationDao;

	@Override
	public Dao<WaybillConsumableRelation> getDao() {
		return this.waybillConsumableRelationDao;
	}

    @Override
    public  List<WaybillConsumableExportDto> queryByWaybillCodes(List<String> waybillCodes) {
        return waybillConsumableRelationDao.queryByWaybillCodes(waybillCodes);
    }
}
