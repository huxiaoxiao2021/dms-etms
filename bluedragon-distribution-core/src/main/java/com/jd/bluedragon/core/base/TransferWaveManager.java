package com.jd.bluedragon.core.base;

import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveDto;
import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveQueryCondition;


/**
 * 波次服务包装接口
 *
 * @author hujiping
 * @date 2021/6/3 9:55 下午
 */
public interface TransferWaveManager {

    /**
     * 根据条件查询最近已结束波次信息
     * @param queryCondition
     * @return
     */
    TransferWaveDto queryTransferWaveByQueryCondition(TransferWaveQueryCondition queryCondition);
}
