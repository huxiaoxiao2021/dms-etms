package com.jd.bluedragon.distribution.integral.controller;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.integral.domain.JyIntegralDetailDTO;
import com.jd.bluedragon.distribution.integral.domain.JyIntegralDetailQuery;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import com.jd.tp.common.utils.Objects;
import com.jdl.jy.flat.entity.personalIntegralStatistics.JyIntegralDTO;
import com.jdl.jy.flat.query.personalIntegralStatistics.JyIntegralQuery;
import org.apache.commons.collections4.CollectionUtils;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.List;

/**
 * @author liuluntao1
 * @description
 * @date 2022/12/19
 */
@Controller
@RequestMapping("integral")
public class IntegralController {


    @Autowired
    IntegralService integralService;

    @RequestMapping("overview")
    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(@RequestBody JyIntegralDetailQuery query){
        return integralService.getSimpleJyIntegralInfo(query);
    }

    @RequestMapping("baseScore")
    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(@RequestBody JyIntegralDetailQuery query){
        return integralService.getJyBaseScoreDetail(query);
    }

    @RequestMapping("coefficient")
    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(@RequestBody JyIntegralDetailQuery query){
        return integralService.getJyIntegralCoefficientDetail(query);
    }

}
