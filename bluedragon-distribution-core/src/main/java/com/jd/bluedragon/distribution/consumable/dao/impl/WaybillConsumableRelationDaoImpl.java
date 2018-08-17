package com.jd.bluedragon.distribution.consumable.dao.impl;

import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.dao.WaybillConsumableRelationDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

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


}
