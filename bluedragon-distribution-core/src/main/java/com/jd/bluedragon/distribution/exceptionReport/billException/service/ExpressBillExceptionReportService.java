package com.jd.bluedragon.distribution.exceptionReport.billException.service;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FirstSiteVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.ReportTypeVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 14:43
 */
public interface ExpressBillExceptionReportService {
    /**
     * 面单异常举报提交
     * @param reportRequest
     * @return
     */
    JdCResponse<Boolean> reportExpressBillException(ExpressBillExceptionReportRequest reportRequest);

    /**
     * 通过包裹号 获取运单始发站点(贴面单的地方)
     * @param packageCode
     * @return
     */
    JdCResponse<FirstSiteVo> getFirstSiteByPackageCode(String packageCode);

    /**
     * 获取所有面单举报类型
     * @return
     */
    JdCResponse<Map<Integer,String>> getAllExceptionReportType();

    /**
     * 提供给安卓的面单举报类型 旧接口
     * @return
     */
    JdCResponse<List<ExpressBillExceptionReportTypeEnum>> getAllExceptionReportTypeList();


    /**
     * 获取所有面单举报类型-提供给安卓-新接口
     */
    JdCResponse<List<ReportTypeVo>> getAllExceptionReportTypeListNew();

    /**
     * 查询包裹是否举报过
     * @param packageCode
     * @return
     */
    boolean selectPackageIsReport(String packageCode);

}
    
