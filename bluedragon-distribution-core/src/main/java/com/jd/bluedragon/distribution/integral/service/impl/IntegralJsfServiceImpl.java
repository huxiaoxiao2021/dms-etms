//package com.jd.bluedragon.distribution.integral.service.impl;
//
//import com.jd.bluedragon.common.dto.base.response.JdCResponse;
//import com.jd.bluedragon.common.dto.integral.request.IntegralRequest;
//import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailDTO;
//import com.jd.bluedragon.common.dto.integral.response.JyIntegralDetailQuery;
//import com.jd.bluedragon.common.dto.integral.response.JyIntroductionDTO;
//import com.jd.bluedragon.distribution.integral.service.IntegralJsfService;
//import com.jd.bluedragon.distribution.integral.service.IntegralService;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//
///**
// * @author liuluntao1
// * @description
// * @date 2022/12/22
// */
//@Service("integralJsfService")
//public class IntegralJsfServiceImpl implements IntegralJsfService {
//
//    @Autowired
//    IntegralService integralService;
//
//    @Override
//    public JdCResponse<JyIntegralDetailDTO> getSimpleJyIntegralInfo(IntegralRequest request) {
//        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
//        query.setUserCode(request.getUser().getUserErp());
//        query.setQueryDate(request.getQueryDate());
//        return integralService.getSimpleJyIntegralInfo(query);
//    }
//
//    @Override
//    public JdCResponse<JyIntegralDetailDTO> getJyBaseScoreDetail(IntegralRequest request) {
//        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
//        query.setUserCode(request.getUser().getUserErp());
//        query.setQueryDate(request.getQueryDate());
//        return integralService.getJyBaseScoreDetail(query);
//    }
//
//    @Override
//    public JdCResponse<JyIntegralDetailDTO> getJyIntegralCoefficientDetail(IntegralRequest request) {
//        JyIntegralDetailQuery query = new JyIntegralDetailQuery();
//        query.setUserCode(request.getUser().getUserErp());
//        query.setQueryDate(request.getQueryDate());
//        return integralService.getJyBaseScoreDetail(query);
//    }
//
//    @Override
//    public JdCResponse<JyIntroductionDTO> getJyIntegralIntroduction() {
//        return integralService.getJyIntegralIntroduction();
//    }
//}
