package com.jd.bluedragon.external.gateway.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FirstSiteVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;


import java.util.List;

public interface ExpressBillExceptionReportGatewayService {


    /**
     * 面单异常举报
     * @return
     */
    JdCResponse<Boolean> reportExpressBillException(ExpressBillExceptionReportRequest reportRequest);

    /**
     * 通过包裹号获取运单始发地
     * @param packageCode
     * @return
     */
    JdCResponse<FirstSiteVo> getFirstSiteByPackageCode(String packageCode);

    /**
     * 获取所有举报类型
     * @return
     */
    JdCResponse<List<ExpressBillExceptionReportTypeEnum>>  getAllExceptionReportType();
}
