package com.jd.bluedragon.distribution.jy.service.exception;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.jyexpection.request.ExpUploadScanReq;
import com.jd.bluedragon.common.dto.operation.workbench.enums.JyExpSourceEnum;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jdl.basic.api.domain.position.PositionDetailRecord;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/3/8 18:13
 * @Description: 三无异常Service
 */
public interface JySanwuExceptionService {

    JdCResponse<Object> uploadScanOfSanwu(ExpUploadScanReq req, PositionDetailRecord position, JyExpSourceEnum source,
                                          BaseStaffSiteOrgDto baseStaffByErp, String bizId);
}
