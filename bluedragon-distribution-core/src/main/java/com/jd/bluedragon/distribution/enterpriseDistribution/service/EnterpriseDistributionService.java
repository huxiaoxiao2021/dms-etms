package com.jd.bluedragon.distribution.enterpriseDistribution.service;

import com.jd.bluedragon.distribution.enterpriseDistribution.domain.QualityInspectionQueryCondition;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDetailDto;
import com.jd.bluedragon.distribution.enterpriseDistribution.dto.QualityInspectionDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;

import java.io.BufferedWriter;

/**
 * @Description
 * @Author chenjunyan
 * @Date 2022/6/15
 */
public interface EnterpriseDistributionService {

    /**
     * 查询质检分页列表
     * @param condition
     * @return
     */
    PagerResult<QualityInspectionDto> queryQualityInspectionPage(QualityInspectionQueryCondition condition);

    /**
     * 查询质检明细分页列表
     * @param condition
     * @return
     */
    PagerResult<QualityInspectionDetailDto> queryQualityInspectionDetailPage(QualityInspectionQueryCondition condition);

    /**
     * 导出
     * @param condition
     * @param innerBfw
     */
    void export(QualityInspectionQueryCondition condition, BufferedWriter innerBfw);
}
