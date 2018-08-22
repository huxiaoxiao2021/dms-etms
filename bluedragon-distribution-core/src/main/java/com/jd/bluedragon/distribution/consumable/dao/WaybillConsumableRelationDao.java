package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableExportDto;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.ql.dms.common.web.mvc.api.Dao;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRelationDao
 * @Description: 运单耗材关系表--Dao接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface WaybillConsumableRelationDao extends Dao<WaybillConsumableRelation> {

    /**
     * 根据运单号集合查询耗材明细
     * @param waybillCodes
     * @return
     */
    public List<WaybillConsumableExportDto> queryByWaybillCodes(List<String> waybillCodes);
}
