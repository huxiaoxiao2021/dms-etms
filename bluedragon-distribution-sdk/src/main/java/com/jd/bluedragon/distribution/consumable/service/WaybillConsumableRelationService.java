package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.*;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ql.dms.common.web.mvc.api.Service;

import java.util.List;

/**
 *
 * @ClassName: WaybillConsumableRelationService
 * @Description: 运单耗材关系表--Service接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface WaybillConsumableRelationService extends Service<WaybillConsumableRelation> {

    /**
     * 根据运单号集合查询耗材明细
     * @param waybillCodes
     * @return
     */
    public List<WaybillConsumableExportDto> queryByWaybillCodes(List<String> waybillCodes);


    /**
     * 根据查询条件获取运单耗材明细
     * @param waybillConsumableRelationCondition
     * @return
     */
    PagerResult<WaybillConsumableDetailInfo> queryDetailByInfoPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition);

}
