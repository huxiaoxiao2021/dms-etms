package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;
import com.jd.bluedragon.common.dto.abnormal.DutyDepartmentInfo;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalReportingRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AbnormalReportingGatewayService {

    JdCResponse<List<DmsAbnormalReasonDto>> getAllAbnormalReason(String userErp);

    String uploadExceptionMedia(InputStream inStream, String originalFileName) throws IOException;

    JdCResponse<List<DutyDepartmentInfo>> getDutyDepartment(String barCode, Integer siteCode, String siteName);

    JdCResponse saveAbnormalReportingInfo(AbnormalReportingRequest abnormalReportingRequest);

    JdCResponse querySite(String orgId, String siteName, String siteCode);

    JdCResponse trace(String key, String packageCode);
}
