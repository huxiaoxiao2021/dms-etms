package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.domain.SiteEntity;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalReportingRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface AbnormalReportingGatewayService {

    JdCResponse<List<DmsAbnormalReasonDto>> getAllAbnormalReason(String userErp);

    JdCResponse<String> uploadExceptionImage(HttpServletRequest request, HttpServletResponse response);

    JdCResponse<List<SiteEntity>> getDutyDepartment(String barCode);

    JdCResponse saveAbnormalReportingInfo(AbnormalReportingRequest abnormalReportingRequest);
}
