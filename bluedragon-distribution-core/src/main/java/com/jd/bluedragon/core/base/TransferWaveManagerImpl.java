package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.dms.wb.report.api.ITransferWaveJsfService;
import com.jd.dms.wb.report.api.dto.base.BaseEntity;
import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveDto;
import com.jd.dms.wb.report.api.dto.transferWave.TransferWaveQueryCondition;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 波次服务包装接口实现
 *
 * @author hujiping
 * @date 2021/6/3 9:55 下午
 */
@Service("transferWaveManager")
public class TransferWaveManagerImpl implements TransferWaveManager {

    @Autowired
    private ITransferWaveJsfService transferWaveJsfService;

    @JProfiler(jKey = "DMS.BASE.TransferWaveManagerImpl.queryTransferWaveByQueryCondition", jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public TransferWaveDto queryTransferWaveByQueryCondition(TransferWaveQueryCondition queryCondition) {
        BaseEntity<TransferWaveDto> baseEntity = transferWaveJsfService.queryTransferWaveByQueryCondition(queryCondition);
        return baseEntity == null ? null : baseEntity.getData();
    }
}
