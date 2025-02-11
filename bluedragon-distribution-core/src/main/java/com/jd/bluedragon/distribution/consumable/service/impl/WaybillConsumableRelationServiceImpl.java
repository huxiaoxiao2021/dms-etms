package com.jd.bluedragon.distribution.consumable.service.impl;

import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRelationDao;
import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.bluedragon.distribution.consumable.service.WaybillConsumableRelationService;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.dms.common.web.mvc.BaseService;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

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
		List<WaybillConsumableDetailInfo> results = new ArrayList<>();
		Set<String> leftWaybillCodes = new HashSet<>();

		/* 兼容逻辑：终端包装耗材重塑项目将耗材的基础资料信息维护迁移到终端，所以此处有兼容逻辑，待分拣所有的包装耗材基础资料完全下线之后，可去掉该兼容逻辑，采用新的查询方法 */
		List<WaybillConsumableDetailInfo> resultLists1 = waybillConsumableRelationDao.queryNewByWaybillCodes(waybillCodes);
		for (WaybillConsumableDetailInfo waybillConsumableDetailInfo : resultLists1) {
			if (StringUtils.isEmpty(waybillConsumableDetailInfo.getType())) {
				leftWaybillCodes.add(waybillConsumableDetailInfo.getWaybillCode());
			} else {
				results.add(waybillConsumableDetailInfo);
			}
		}
		if (CollectionUtils.isNotEmpty(leftWaybillCodes)) {
			results.addAll(waybillConsumableRelationDao.queryByWaybillCodes(new ArrayList<String>(leftWaybillCodes)));
		}

        return results;
    }

	@Override
	public PagerResult<WaybillConsumableDetailInfo> queryDetailInfoByPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition) {
		PagerResult<WaybillConsumableDetailInfo> result = waybillConsumableRelationDao.queryNewDetailInfoByPagerCondition(waybillConsumableRelationCondition);

		/* 兼容逻辑：终端包装耗材重塑项目将耗材的基础资料信息维护迁移到终端，所以此处有兼容逻辑，待分拣所有的包装耗材基础资料完全下线之后，可去掉该兼容逻辑，采用新的查询方法 */
		if (result == null || CollectionUtils.isEmpty(result.getRows()) || StringUtils.isEmpty(result.getRows().get(0).getType())) {
			result = waybillConsumableRelationDao.queryDetailInfoByPagerCondition(waybillConsumableRelationCondition);
		}

		if (CollectionUtils.isNotEmpty(result.getRows())) {
			for (WaybillConsumableDetailInfo detailInfo : result.getRows()) {
				if (detailInfo.getVolume() != null) {
					//页面显示 将立方厘米转换为立方米
					detailInfo.setVolume(detailInfo.getVolume().divide(BigDecimal.valueOf(1000000), 3, RoundingMode.HALF_UP));
				}
			}

		}

		return result;
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

	@Override
	public List<WaybillConsumableDetailInfo> getNoConfirmVolumeRecordCount(String waybillCode) {
		return waybillConsumableRelationDao.getNoConfirmVolumeRecordCount(waybillCode);
	}

	@Override
	public void updateByWaybillCode(WaybillConsumableRelationPDADto waybillConsumableRelationPDADto) {
		waybillConsumableRelationDao.updateByWaybillCode(waybillConsumableRelationPDADto);
	}

	@Override
	public WaybillConsumableRelation findByWaybillCodeAndConsumableCode(WaybillConsumableRelation waybillConsumableRelation) {
		return waybillConsumableRelationDao.findByWaybillCodeAndConsumableCode(waybillConsumableRelation);
	}

	@Override
	public void updateByWaybillCodeAndConsumableCode(WaybillConsumableRelation waybillConsumableRelation) {
		waybillConsumableRelationDao.updateByWaybillCodeAndConsumableCode(waybillConsumableRelation);
	}

}
