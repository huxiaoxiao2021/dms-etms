package com.jd.bluedragon.distribution.exceptionReport.billException.service;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;
import com.jd.bluedragon.distribution.exceptionReport.billException.vo.ExpressBillExceptionReportVo;
import com.jd.ql.dms.common.web.mvc.api.PageDto;

import java.util.List;
import java.util.Map;

/**
 * @Author: liming522
 * @Description:  提供给工作台的
 * @Date: create in 2020/12/28 23:20
 */
public interface ExpressBillExceptionReportCenterService {
    /**
     * 面单异常分页查询
     * @param reportRequest
     * @return
     */
    Response<PageDto<ExpressBillExceptionReportVo>> queryPageList(ExpressBillExceptionReportQuery reportRequest);

    /**
     * 返回所有举报类型枚举
     * @return
     */
    Response<Map<Integer,String>> getAllExceptionReportType();

    /**
     * 根据条件查询包裹图片
     *
     * @return
     */
    Response<List<String>> getPicUrlsById(Integer id);
}
    
