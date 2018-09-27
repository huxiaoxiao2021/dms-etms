package com.jd.bluedragon.distribution.consumable.dao.impl;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.springframework.stereotype.Repository;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.dao.DmsConsumableRelationDao;
import com.jd.ql.dms.common.web.mvc.mybatis.BaseDao;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DmsConsumableRelationDaoImpl
 * @Description: 分拣中心耗材关系表--Dao接口实现
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
@Repository("dmsConsumableRelationDao")
public class DmsConsumableRelationDaoImpl extends BaseDao<DmsConsumableRelation> implements DmsConsumableRelationDao {

    @Override
    public List getPackingConsumableInfoByDmsId(Integer dmsId) {
        return this.getSqlSession().selectList(this.getNameSpace() + ".getPackingConsumableInfoByDmsId", dmsId);
    }

    @Override
    public PagerResult queryDetailInfoByPagerCondition(DmsConsumableRelationCondition dmsConsumableRelationCondition) {
        return this.queryByPagerCondition("queryDetailInfoByPagerCondition", dmsConsumableRelationCondition);
    }

    @Override
    public int updateByParams(DmsConsumableRelation dmsConsumableRelation) {
        return this.getSqlSession().update(this.getNameSpace()+ ".updateByParams", dmsConsumableRelation);
    }
}
