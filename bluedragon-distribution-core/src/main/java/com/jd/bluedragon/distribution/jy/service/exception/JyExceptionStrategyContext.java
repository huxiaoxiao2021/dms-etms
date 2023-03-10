package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;
import org.springframework.stereotype.Service;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:30
 * @Description: 异常策略上下文类
 */
@Service("jyExceptionStrategyContext")
public class JyExceptionStrategyContext {


    private JyExceptionServiceStrategy jyExceptionServiceStrategy;

    public void setJyExceptionServiceStrategy(JyExceptionServiceStrategy jyExceptionServiceStrategy) {
        this.jyExceptionServiceStrategy = jyExceptionServiceStrategy;
    }

    public  JdCResponse<Object> uploadScan(ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source,
                                   BaseStaffSiteOrgDto baseStaffByErp, String bizId){
        return jyExceptionServiceStrategy.uploadScan(req, position, source, baseStaffByErp, bizId);
    }
}
