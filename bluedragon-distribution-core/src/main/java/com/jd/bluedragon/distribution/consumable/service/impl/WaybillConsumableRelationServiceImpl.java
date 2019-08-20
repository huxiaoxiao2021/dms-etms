package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRelationDao;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailInfo;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public  List<WaybillConsumableDetailInfo> queryByWaybillCodes(List<String> waybillCodes) {
        return waybillConsumableRelationDao.queryByWaybillCodes(waybillCodes);
    }

	@Override
	public PagerResult<WaybillConsumableDetailInfo> queryDetailInfoByPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition) {
		return waybillConsumableRelationDao.queryDetailInfoByPagerCondition(waybillConsumableRelationCondition);
	}

	@Override
	public int updatePackUserErpByWaybillCode(List<String> waybillCodeList, String packUserErp, LoginUser loginUser) {
		Map<String, Object> params = new HashMap<>();
		params.put("waybillCodeList", waybillCodeList);
		params.put("packUserErp", packUserErp);
		params.put("operateUserCode", loginUser.getStaffNo().toString());
		params.put("operateUserErp", loginUser.getUserErp());
		params.put("updateTime", new Date());
		params.put("operateTime", new Date());
		return waybillConsumableRelationDao.updatePackUserErpByWaybillCode(params);
	}

	@Override
	public int updatePackUserErpById(List<Long> ids, String packUserErp, LoginUser loginUser) {
		Map<String, Object> params = new HashMap<>();
		params.put("ids", ids);
		params.put("packUserErp", packUserErp);
		params.put("operateUserCode", loginUser.getStaffNo().toString());
		params.put("operateUserErp", loginUser.getUserErp());
		params.put("updateTime", new Date());
		params.put("operateTime", new Date());
		return waybillConsumableRelationDao.updatePackUserErpById(params);
	}

    @Override
    public int getNoPackUserErpRecordCount(String waybillCode) {
        return waybillConsumableRelationDao.getNoPackUserErpRecordCount(waybillCode);
    }
}
