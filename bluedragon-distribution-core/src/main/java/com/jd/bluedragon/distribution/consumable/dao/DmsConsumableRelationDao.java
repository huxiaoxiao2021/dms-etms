package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.DmsConsumableRelation;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

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

}
