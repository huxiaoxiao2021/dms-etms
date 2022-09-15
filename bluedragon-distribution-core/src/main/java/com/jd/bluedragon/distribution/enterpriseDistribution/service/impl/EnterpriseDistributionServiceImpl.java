package com.jd.bluedragon.distribution.enterpriseDistribution.service.impl;

import com.github.pagehelper.PageInfo;
import com.jd.bluedragon.common.domain.ExportConcurrencyLimitEnum;
import com.jd.bluedragon.common.service.ExportConcurrencyLimitService;
import com.jd.bluedragon.core.base.EnterpriseInspectionManager;
import com.jd.bluedragon.distribution.enterpriseDistribution.domain.QualityInspectionQueryCondition;
import com.jd.bluedragon.distribution.enterpriseDistribution.service.EnterpriseDistributionService;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDetailDto;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDto;
import com.jd.bluedragon.utils.CsvExporterUtils;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ningde.enterprise.request.QualityInspectionDetailRequest;
import com.jd.ningde.enterprise.request.QualityInspectionReportRequest;
import com.jd.ningde.enterprise.response.QualityInspectionDetailResponse;
import com.jd.ningde.enterprise.response.QualityInspectionReportResponse;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.BufferedWriter;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 企配-质检报表
 * @Author chenjunyan
 * @Date 2022/6/15
 */
@Service
@Slf4j
public class EnterpriseDistributionServiceImpl implements EnterpriseDistributionService {

    @Resource
    private ExportConcurrencyLimitService exportConcurrencyLimitService;

    @Resource
    private EnterpriseInspectionManager enterpriseInspectionManager;

    /**
     * 查询质检分页列表
     * @param condition
     * @return
     */
    @Override
    public PagerResult<QualityInspectionDto> queryQualityInspectionPage(QualityInspectionQueryCondition condition) {
        QualityInspectionReportRequest reportRequest = new QualityInspectionReportRequest();
        BeanUtils.copyProperties(condition, reportRequest);
        reportRequest.setPageSize(condition.getLimit());
        reportRequest.setPageNum(condition.getOffset()/condition.getLimit() + 1);
        PageInfo<QualityInspectionReportResponse> pageInfo =  enterpriseInspectionManager.queryQualityInspectionPage(reportRequest);
        if (pageInfo != null) {
            PagerResult<QualityInspectionDto> pagerResult = new PagerResult<>();
            List<QualityInspectionDto> dtoList = new ArrayList<>(pageInfo.getList().size());
            for (QualityInspectionReportResponse response : pageInfo.getList()) {
                QualityInspectionDto dto = new QualityInspectionDto();
                BeanUtils.copyProperties(response, dto);
                dtoList.add(dto);
            }
            pagerResult.setTotal((int)pageInfo.getTotal());
            pagerResult.setRows(dtoList);
            return pagerResult;
        }
        return null;
    }

    /**导出调用繁忙,请稍后重试
     * 查询质检明细分页列表
     * @param condition
     * @return
     */
    @Override
    public PagerResult<QualityInspectionDetailDto> queryQualityInspectionDetailPage(QualityInspectionQueryCondition condition) {
        QualityInspectionDetailRequest request = new QualityInspectionDetailRequest();
        request.setWaybillNo(condition.getWaybillNo());
        request.setPageSize(condition.getLimit());
        request.setPageNum(condition.getOffset()/condition.getLimit() + 1);
        PageInfo<QualityInspectionDetailResponse> pageInfo = enterpriseInspectionManager.queryQualityInspectionDetailPage(request);
        if (pageInfo != null) {
            PagerResult<QualityInspectionDetailDto> pagerResult = new PagerResult<>();
            List<QualityInspectionDetailDto> dtoList = new ArrayList<>(pageInfo.getList().size());
            for (QualityInspectionDetailResponse response : pageInfo.getList()) {
                QualityInspectionDetailDto dto = new QualityInspectionDetailDto();
                BeanUtils.copyProperties(response, dto);
                dtoList.add(dto);
            }
            pagerResult.setTotal((int)pageInfo.getTotal());
            pagerResult.setRows(dtoList);
            return pagerResult;
        }
        return null;
    }



    /**
     * 导出
     * @param condition
     * @param innerBfw
     */
    @Override
    public void export(QualityInspectionQueryCondition condition, BufferedWriter innerBfw) {
        try {
            long start = System.currentTimeMillis();
            // 写入表头
            Map<String, String> headerMap = getHeaderMap();
            CsvExporterUtils.writeTitleOfCsv(headerMap, innerBfw, headerMap.values().size());
            int pageSize = 1000;
            int maxPage = 2000;
            int index = 1;
            int totalSize = 0;
            while (index <= maxPage) {
                condition.setLimit(pageSize);
                condition.setOffset((index-1)*pageSize);
                PagerResult<QualityInspectionDto>  pageInfo = queryQualityInspectionPage(condition);
                if(pageInfo == null || CollectionUtils.isEmpty(pageInfo.getRows())){
                    log.warn("查询抽检明细数据为空!");
                    break;
                }
                index ++;
                CsvExporterUtils.writeCsvByPage(innerBfw, headerMap, pageInfo.getRows());
                totalSize += pageInfo.getRows().size();
                if(pageInfo.getRows().size() < pageSize){
                    break;
                }
            }
            long end = System.currentTimeMillis();
            exportConcurrencyLimitService.addBusinessLog(JsonHelper.toJson(condition), ExportConcurrencyLimitEnum.ENTERPRISE_DISTRIBUTION_QUALITY_INSPECTION.getName(), end-start, totalSize);
        }catch (Exception e){
            log.error("分页获取导出数据失败",e);
        }
    }

    private Map<String, String> getHeaderMap() {
        Map<String, String> headerMap = new LinkedHashMap<>();
        headerMap.put("waybillNo","运单号");
        headerMap.put("optStatusName","状态");
        headerMap.put("totalQty","SKU总件数");
        headerMap.put("checkedQty","已质检SKU数量");
        headerMap.put("exceptionReasonName","异常原因");
        headerMap.put("addValueServiceName","增值服务");
        headerMap.put("createTime","创建时间");
        headerMap.put("updateUser","操作人");
        headerMap.put("updateTime","更新时间");
        headerMap.put("exceptionRemark","异常备注");
        return headerMap;
    }

}
