package com.jd.bluedragon.distribution.consumable.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRecordDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRecordDaoImpl
 * @Description: 运单耗材记录表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Repository("waybillConsumableRecordDao")
public class WaybillConsumableRecordDaoImpl extends BaseDao<WaybillConsumableRecord> implements WaybillConsumableRecordDao {

    @Override
    public WaybillConsumableRecord queryOneByCondition(WaybillConsumableRecord condition) {
        return sqlSession.selectOne(nameSpace+".queryByWaybillCode", condition);
    }

    @Override
    public int updateByIds(List<WaybillConsumableRecord> records) {
        return sqlSession.update(nameSpace+".updateByIds", records);
    }
}
