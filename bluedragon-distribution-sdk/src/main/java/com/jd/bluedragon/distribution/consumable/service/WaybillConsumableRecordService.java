package com.jd.bluedragon.distribution.consumable.service;

import com.jd.bluedragon.distribution.consumable.domain.WaybillConsumableRecord;
import com.jd.ql.dms.common.web.mvc.api.Service;

/**
 *
 * @ClassName: WaybillConsumableRecordService
 * @Description: 运单耗材记录表--Service接口
 * @author wuyoude
 * @date 2018年08月17日 10:14:27
 *
 */
public interface WaybillConsumableRecordService extends Service<WaybillConsumableRecord> {

    //运单耗材记录表：未处理状态
    int UNTREATED_STATE = 0;
    //运单耗材记录表：已处理状态
    int TREATED_STATE = 1;

    /**
     * 根据运单号查询一条记录
     * @param waybillCode
     * @return
     */
    public WaybillConsumableRecord queryOneByWaybillCode(String waybillCode);
}
