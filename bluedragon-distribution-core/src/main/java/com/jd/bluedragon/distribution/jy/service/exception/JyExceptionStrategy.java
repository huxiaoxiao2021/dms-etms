package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/9 21:18
 * @Description: 异常策略类
 */
public abstract class JyExceptionStrategy {

    public abstract  JdCResponse<Object> uploadScan(ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source,
                                                    BaseStaffSiteOrgDto baseStaffByErp, String bizId);

}
