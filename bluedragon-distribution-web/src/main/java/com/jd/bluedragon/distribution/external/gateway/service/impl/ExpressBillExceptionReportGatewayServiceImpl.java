package com.jd.bluedragon.distribution.external.gateway.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FirstSiteVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.ReportTypeVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportService;
import com.jd.bluedragon.external.gateway.service.ExpressBillExceptionReportGatewayService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Author: liming522
 * @Description:  面单异常举报提供给安卓的后台接口
 * @Date: create in 2020/12/21 14:48
 */
public class ExpressBillExceptionReportGatewayServiceImpl implements ExpressBillExceptionReportGatewayService {

    @Autowired
    private ExpressBillExceptionReportService expressBillExceptionReportService;

    @Override
    public JdCResponse<FirstSiteVo> getFirstSiteByPackageCode(String packageCode){
        return expressBillExceptionReportService.getFirstSiteByPackageCode(packageCode);
    }

    @Override
    public JdCResponse<List<ReportTypeVo>> getAllExceptionReportTypeNew() {
        return expressBillExceptionReportService.getAllExceptionReportTypeListNew();
    }

    @Override
    public JdCResponse<List<ExpressBillExceptionReportTypeEnum>> getAllExceptionReportType(){
        return expressBillExceptionReportService.getAllExceptionReportTypeList();
    }

    /**
     * 面单异常举报-接口
     * @param reportRequest
     * @return
     */
    @Override
    public JdCResponse<Boolean> reportExpressBillException(ExpressBillExceptionReportRequest reportRequest) {
        return expressBillExceptionReportService.reportExpressBillException(reportRequest);
    }
}
    
