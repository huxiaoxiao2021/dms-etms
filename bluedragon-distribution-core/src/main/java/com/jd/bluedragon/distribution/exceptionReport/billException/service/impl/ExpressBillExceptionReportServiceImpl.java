package com.jd.bluedragon.distribution.exceptionReport.billException.service.impl;

import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportService;
import com.jd.bluedragon.distribution.exceptionReport.billException.vo.ExpressBillExceptionReportVo;
import com.jd.etms.sdk.util.DateUtil;
import com.jd.fastjson.JSON;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/22 15:15
 */
@Service("dmsExpressBillExceptionReportService")
public class ExpressBillExceptionReportServiceImpl implements ExpressBillExceptionReportService {
     private final Logger log   =LoggerFactory.getLogger(ExpressBillExceptionReportServiceImpl.class);

     @Autowired
     private ExpressBillExceptionReportDao expressBillExceptionReportDao;

    /**
     * 提供给工作台分页查询
     * @param query
     * @return
     */
    @Override
    public Response<PageDto<ExpressBillExceptionReportVo>> queryPageList(ExpressBillExceptionReportQuery query) {
        if(log.isInfoEnabled()) {
            log.info("ExpressBillExceptionReportServiceImpl.queryPageList param: {}", JSON.toJSONString(query));
        }
        //返回给调用方的结果对象
        Response<PageDto<ExpressBillExceptionReportVo>> result = new Response<>();
        result.toSucceed();
        PageDto<ExpressBillExceptionReportVo> pageData = new PageDto<>();
        pageData.setCurrentPage(query.getPageNumber());
        pageData.setPageSize(query.getLimit());
        // 数据集合
        List<ExpressBillExceptionReportVo> dataList = new ArrayList<>();
        long total = 0;
        query.setYn(1);
        try {
            Response<Boolean> checkResult = this.checkParam(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            total = expressBillExceptionReportDao.queryCount(query);
            if(total > 0){
                List<ExpressBillExceptionReport> recordList = expressBillExceptionReportDao.queryList(query);
                for (ExpressBillExceptionReport expressBillExceptionReport : recordList) {
                    dataList.add(this.generateExpressBillExceptionReportVo(expressBillExceptionReport));
                }
            }
        }catch (Exception e){
            result.toError(e.getMessage());
            log.error("ExpressBillExceptionReportServiceImpl.queryPageList exception: {}", e.getMessage(), e);
        }
        pageData.setTotalRow((int)total);
        pageData.setResult(dataList);
        result.setData(pageData);
        return result;
    }

    /**
     * 组装返回数据对象
     * @param expressBillExceptionReport
     * @return
     */
    private ExpressBillExceptionReportVo generateExpressBillExceptionReportVo(ExpressBillExceptionReport expressBillExceptionReport) {
        ExpressBillExceptionReportVo vo = new ExpressBillExceptionReportVo();
        BeanUtils.copyProperties(expressBillExceptionReport,vo);
        List<String> imgUrlList = new ArrayList<>();
        if(StringUtils.isNotBlank(vo.getReportImgUrls())){
            imgUrlList = JSON.parseArray(vo.getReportImgUrls(), String.class);
        }
        vo.setReportImgUrlList(imgUrlList);
        return  vo;
    }

    private Response<Boolean> checkParam(ExpressBillExceptionReportQuery query) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if(StringUtils.isNotEmpty(query.getQueryStartTimeStr())){
            query.setQueryStartTime(DateUtil.parse(query.getQueryStartTimeStr(), DateUtil.FORMAT_DATE_TIME));
        }
        if(StringUtils.isNotEmpty(query.getQueryEndTimeStr())){
            query.setQueryEndTime(DateUtil.parse(query.getQueryEndTimeStr(), DateUtil.FORMAT_DATE_TIME));
        }
        return  response;
    }
}
    
