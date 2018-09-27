package com.jd.bluedragon.distribution.consumable.dao.impl;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailInfo;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRelationDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRelationDaoImpl
 * @Description: 运单耗材关系表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Repository("waybillConsumableRelationDao")
public class WaybillConsumableRelationDaoImpl extends BaseDao<WaybillConsumableRelation> implements WaybillConsumableRelationDao {

    @Override
    public List<WaybillConsumableDetailInfo> queryByWaybillCodes(List<String> waybillCodes) {
        return sqlSession.selectList(this.nameSpace+".queryByWaybillCodes", waybillCodes);
    }

    @Override
    public PagerResult<WaybillConsumableDetailInfo> queryDetailInfoByPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition) {
        return this.queryByPagerCondition("queryDetailInfoByPagerCondition", waybillConsumableRelationCondition);
    }
}
