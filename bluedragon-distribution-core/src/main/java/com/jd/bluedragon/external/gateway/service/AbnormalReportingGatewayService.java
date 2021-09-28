package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.abnormal.Dept;
import com.jd.bluedragon.common.dto.abnormal.DeptType;
import com.jd.bluedragon.common.dto.abnormal.DmsAbnormalReasonDto;
import com.jd.bluedragon.common.dto.abnormal.DutyDepartmentInfo;
import com.jd.bluedragon.common.dto.abnormal.request.AbnormalReportingRequest;
import com.jd.bluedragon.common.dto.abnormal.request.DeptQueryRequest;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.dms.utils.AreaData;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface AbnormalReportingGatewayService {

    JdCResponse<List<DmsAbnormalReasonDto>> getAllAbnormalReason(String userErp);

    String uploadExceptionMedia(InputStream inStream, String originalFileName) throws IOException;

    JdCResponse<List<DutyDepartmentInfo>> getDutyDepartment(String barCode, Integer siteCode, String siteName);

    JdCResponse saveAbnormalReportingInfo(AbnormalReportingRequest abnormalReportingRequest);

    JdCResponse querySite(String orgId, String siteName, String siteCode);
    /**
     * 获取区域列表
     * @return
     */
    JdCResponse<List<AreaData>> getAreaDataList();
    /**
     * 获取责任部门类型列表
     * @return
     */
    JdCResponse<List<DeptType>> getDeptTypes();
    /**
     * 查询责任部门列表
     * @param queryRequest
     * @return
     */
    JdCResponse<List<Dept>> getDept(DeptQueryRequest queryRequest);
}
