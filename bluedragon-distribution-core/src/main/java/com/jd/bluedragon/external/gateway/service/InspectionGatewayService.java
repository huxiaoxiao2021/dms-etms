package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.base.response.JdVerifyResponse;
import com.jd.bluedragon.common.dto.inspection.request.InspectionRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckResultDto;
import com.jd.bluedragon.common.dto.inspection.response.InspectionCheckWaybillTypeRequest;
import com.jd.bluedragon.common.dto.inspection.response.InspectionResultDto;
import com.jd.bluedragon.common.dto.waybill.request.ThirdWaybillReq;
import com.jd.bluedragon.distribution.api.request.HintCheckRequest;

/**
 * 验货相关
 * 发布到物流网关 由安卓调用
 * @author : xumigen
 * @date : 2019/6/14
 */
public interface InspectionGatewayService {

    JdCResponse<InspectionResultDto> getStorageCode(String packageBarOrWaybillCode, Integer siteCode);

    JdCResponse<InspectionCheckResultDto> hintCheck(HintCheckRequest request);

    JdCResponse<String> getThirdWaybillPackageCode(ThirdWaybillReq request);

    JdVerifyResponse checkWaybillType(InspectionCheckWaybillTypeRequest request);

    JdVerifyResponse<InspectionCheckResultDto> checkBeforeInspection(InspectionRequest request);
}
