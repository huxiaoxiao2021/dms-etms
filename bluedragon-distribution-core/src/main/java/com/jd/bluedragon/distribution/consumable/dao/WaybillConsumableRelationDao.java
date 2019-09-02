package com.jd.bluedragon.distribution.consumable.dao;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailInfo;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
import com.jd.ql.dms.common.web.mvc.api.Dao;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.util.List;
import java.util.Map;

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
    public List<WaybillConsumableDetailInfo> queryByWaybillCodes(List<String> waybillCodes);


    /**
     * 根据查询条件获取运单耗材明细
     * @param waybillConsumableRelationCondition
     * @return
     */
    PagerResult<WaybillConsumableDetailInfo> queryDetailInfoByPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition);

    /**
     * 根据运单号批量更新打包人
     * @param
     * @return
     */
    int updatePackUserErpByWaybillCode(Map<String, Object> params);

    /**
     * 根据id批量更新打包人
     * @param
     * @return
     */
    int updatePackUserErpById(Map<String, Object> params);

    /**
     * 通过运单号查询未录入打包人ERP的耗材数
     * @param
     * @return
     */
    int getNoPackUserErpRecordCount(String waybillCode);

}
