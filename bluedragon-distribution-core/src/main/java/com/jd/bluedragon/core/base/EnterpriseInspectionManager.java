package com.jd.bluedragon.core.base;

import com.github.pagehelper.PageInfo;
import com.jd.ningde.enterprise.request.QualityInspectionDetailRequest;
import com.jd.ningde.enterprise.request.QualityInspectionReportRequest;
import com.jd.ningde.enterprise.response.QualityInspectionDetailResponse;
import com.jd.ningde.enterprise.response.QualityInspectionReportResponse;

/**
 * @Description 企配质检服务
 * @Author chenjunyan
 * @Date 2022/6/17
 */
public interface EnterpriseInspectionManager {

    /**
     * 查询质检分页列表
     * @param reportRequest
     * @return
     */
    PageInfo<QualityInspectionReportResponse> queryQualityInspectionPage(QualityInspectionReportRequest reportRequest);

    /**
     * 查询质检明细分页列表
     * @param request
     * @return
     */
    PageInfo<QualityInspectionDetailResponse> queryQualityInspectionDetailPage(QualityInspectionDetailRequest request);
}
