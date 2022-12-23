package com.jd.bluedragon.distribution.integral.domain;


import com.jdl.jy.flat.api.PersonalIntegralStatistics.IJyPersonalIntegralStatisticsJSFService;
import com.jdl.jy.flat.base.ServiceResult;
import com.jdl.jy.flat.dto.personalIntegralStatistics.JyIntegralDTO;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Service
public class IntegralProxy {

    private static final Logger log = LoggerFactory.getLogger(IntegralProxy.class);

    @Autowired
    IJyPersonalIntegralStatisticsJSFService personalIntegralJSFService;

    public List<JyIntegralDTO> querySumByUserCode(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.querySumByUserCode(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    public List<JyIntegralDTO> queryIntegralPersonalByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryIntegralPersonalByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    public List<JyIntegralDTO> queryIntegralPersonalQuotaByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryIntegralPersonalQuotaByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    public List<JyIntegralDTO> queryFlatIntegralScoreRuleByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryFlatIntegralScoreRuleByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

    public List<JyIntegralDTO> queryFlatIntegralQuotaByCondition(JyIntegralQuery query){
        ServiceResult<List<JyIntegralDTO>> serviceResult = personalIntegralJSFService.queryFlatIntegralQuotaByCondition(query);
        if (serviceResult.getSuccess()) {
            return serviceResult.getData();
        } else {
            throw new RuntimeException();
        }
    }

}
