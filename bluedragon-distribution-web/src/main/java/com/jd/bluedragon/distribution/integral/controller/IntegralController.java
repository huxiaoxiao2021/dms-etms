package com.jd.bluedragon.distribution.integral.controller;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailDTO;
import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailQuery;
import com.jd.bluedragon.distribution.integral.service.IntegralService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;


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

    @RequestMapping(value = "overview", method = RequestMethod.POST)
    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(@RequestBody IntegralRequest request){
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getSimpleJyIntegralInfo(query);
    }

    @RequestMapping(value =  "baseScore", method = RequestMethod.POST)
    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(@RequestBody IntegralRequest request){
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyBaseScoreDetail(query);
    }

    @RequestMapping(value = "coefficient", method = RequestMethod.POST)
    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(@RequestBody IntegralRequest request){
        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
        query.setUserCode(request.getUser().getUserErp());
        query.setQueryDate(request.getQueryDate());
        return integralService.getJyIntegralCoefficientDetail(query);
    }

}
