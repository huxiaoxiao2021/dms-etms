package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelationCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;
import java.util.Map;

/**
 *
 * @ClassName: DmsConsumableRelationDao
 * @Description: 分拣中心耗材关系表--Dao接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface DmsConsumableRelationDao extends Dao<DmsConsumableRelation> {

    /**
     * 根据分拣中心查询信息
     *
     * @param dmsId
     * @return
     */
    List getPackingConsumableInfoByDmsId(Integer dmsId);

    /**
     * 根据条件查询关系明细信息
     *
     * @param dmsConsumableRelationCondition
     * @return
     */
    PagerResult queryDetailInfoByPagerCondition(DmsConsumableRelationCondition dmsConsumableRelationCondition);

    /**
     * 更新
     *
     * @param
     * @return
     */
    int updateByParams(DmsConsumableRelation dmsConsumableRelation);

}
