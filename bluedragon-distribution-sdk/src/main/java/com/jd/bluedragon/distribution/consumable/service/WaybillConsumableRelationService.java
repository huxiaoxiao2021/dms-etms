package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.api.domain.LoginUser;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableDetailInfo;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumablePackUserRequest;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelation;
import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRelationCondition;
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
    public List<WaybillConsumableDetailInfo> queryByWaybillCodes(List<String> waybillCodes);


    /**
     * 根据查询条件获取运单耗材明细
     * @param waybillConsumableRelationCondition
     * @return
     */
    PagerResult<WaybillConsumableDetailInfo> queryDetailInfoByPagerCondition(WaybillConsumableRelationCondition waybillConsumableRelationCondition);

    /**
     * 通过运单号更新打包人
     * @param
     * @return
     */
    int updatePackUserErpByWaybillCode(List<String> waybillCodeList, String userErp, LoginUser loginUser);

    /**
     * 通过id更新打包人
     * @param
     * @return
     */
    int updatePackUserErpById(List<Long> ids, String packUserErp, LoginUser loginUser);

    /**
     * 通过运单号查询未录入打包人ERP的耗材数
     * @param
     * @return
     */
    int getNoPackUserErpRecordCount(String waybillCode);

}
