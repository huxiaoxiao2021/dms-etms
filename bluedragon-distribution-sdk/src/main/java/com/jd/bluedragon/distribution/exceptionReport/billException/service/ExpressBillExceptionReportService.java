package com.jd.bluedragon.distribution.exceptionReport.billException.service;


import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;
import com.jd.bluedragon.distribution.exceptionReport.billException.vo.ExpressBillExceptionReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/21 14:43
 */
public interface ExpressBillExceptionReportService {
    /**
     * 面单异常分页查询
     * @param reportRequest
     * @return
     */
    Response<PageDto<ExpressBillExceptionReportVo>> queryPageList(ExpressBillExceptionReportQuery reportRequest);
}
    
