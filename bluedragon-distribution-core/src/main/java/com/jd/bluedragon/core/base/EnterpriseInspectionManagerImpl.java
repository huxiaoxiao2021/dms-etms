package com.jd.bluedragon.core.base;

import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.jd.ningde.enterprise.request.QualityInspectionDetailRequest;
import com.jd.ningde.enterprise.request.QualityInspectionReportRequest;
import com.jd.ningde.enterprise.response.BaseResponse;
import com.jd.ningde.enterprise.response.QualityInspectionDetailResponse;
import com.jd.ningde.enterprise.response.QualityInspectionReportResponse;
import com.jd.ningde.enterprise.service.EnterpriseInspectionJsfService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Description 企配质检服务
 * @Author chenjunyan
 * @Date 2022/6/17
 */
@Service
@Slf4j
public class EnterpriseInspectionManagerImpl implements EnterpriseInspectionManager {

    /**
     * 企配质检jsf服务
     */
    @Resource
    private EnterpriseInspectionJsfService enterpriseInspectionJsfService;

    /**
     * 查询质检分页列表
     * @param reportRequest
     * @return
     */
    @Override
    public PageInfo<QualityInspectionReportResponse> queryQualityInspectionPage(QualityInspectionReportRequest reportRequest) {
        BaseResponse<PageInfo<QualityInspectionReportResponse>> response =  enterpriseInspectionJsfService.queryQualityInspectionPage(reportRequest);
        if (response != null && response.getCode() == 200) {
            return response.getData();
        } else {
            log.error("查询企配质检报表列表服务入参:{},返回异常:{}", JSONObject.toJSONString(reportRequest), JSONObject.toJSONString(response));
        }
        return null;
    }

    /**
     * 查询质检明细分页列表
     * @param request
     * @return
     */
    @Override
    public PageInfo<QualityInspectionDetailResponse> queryQualityInspectionDetailPage(QualityInspectionDetailRequest request) {
        BaseResponse<PageInfo<QualityInspectionDetailResponse>> response = enterpriseInspectionJsfService.queryQualityInspectionDetailPage(request);
        if (response != null && response.getCode() == 0) {
            return response.getData();
        } else {
            log.error("查询企配质检报表明细服务入参:{},返回异常:{}", JSONObject.toJSONString(request), JSONObject.toJSONString(response));
        }
        return null;
    }
}
