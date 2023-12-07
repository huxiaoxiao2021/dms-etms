package com.jd.bluedragon.distribution.jy.service.picking;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;

/**
 * @Author zhengchengfa
 * @Date 2023/12/6 20:29
 * @Description
 */
public interface JyPickingSendRecordService {

    /**
     * 根据待扫单据查询待提货任务bizId
     * @param siteCode 待提货场地
     * @param barCode 待提货单据【包裹号或箱号...】
     * @return
     */
    InvokeResult<String> fetchPickingBizIdByBarCode(Long siteCode, String barCode);
}
