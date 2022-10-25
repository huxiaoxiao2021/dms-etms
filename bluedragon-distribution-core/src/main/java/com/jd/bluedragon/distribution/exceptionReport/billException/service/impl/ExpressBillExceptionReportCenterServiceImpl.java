package com.jd.bluedragon.distribution.exceptionReport.billException.service.impl;

import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FaceFirstAbnormalType;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FaceSecondAbnormalType;
import com.jd.bluedragon.distribution.api.Response;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.request.ExpressBillExceptionReportQuery;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportCenterService;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportService;
import com.jd.bluedragon.distribution.exceptionReport.billException.vo.ExpressBillExceptionReportVo;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.sdk.util.DateUtil;
import com.alibaba.fastjson.JSON;
import com.jd.jddl.executor.function.scalar.filter.In;
import com.jd.ql.dms.common.web.mvc.api.PageDto;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: liming522
 * @Description:
 * @Date: create in 2020/12/28 23:28
 */
@Service("expressBillExceptionReportCenterService")
public class ExpressBillExceptionReportCenterServiceImpl implements ExpressBillExceptionReportCenterService {

    private static final Logger log  =  LoggerFactory.getLogger(ExpressBillExceptionReportCenterServiceImpl.class);

    @Autowired
    private ExpressBillExceptionReportDao expressBillExceptionReportDao;

    @Autowired
    private ExpressBillExceptionReportService expressBillExceptionReportService;

    @Override
    public Response<PageDto<ExpressBillExceptionReportVo>> queryPageList(ExpressBillExceptionReportQuery query) {
        if(log.isInfoEnabled()) {
            log.info("ExpressBillExceptionReportCenterServiceImpl.queryPageList param: {}", JSON.toJSONString(query));
        }
        //返回给调用方的结果对象
        Response<PageDto<ExpressBillExceptionReportVo>> result = new Response<>();
        result.toSucceed();
        PageDto<ExpressBillExceptionReportVo> pageData = new PageDto<>();
        pageData.setCurrentPage(query.getBasePagerCondition().getPageNumber());
        pageData.setPageSize(query.getBasePagerCondition().getLimit());
        // 数据集合
        List<ExpressBillExceptionReportVo> dataList = new ArrayList<>();
        long total = 0;
        try {
            Response<Boolean> checkResult = this.checkParam(query);
            if(!checkResult.isSucceed()){
                result.toError(checkResult.getMessage());
                return result;
            }
            query.setYn(1);
            total = expressBillExceptionReportDao.queryCount(query);
            if(total > 0){
                List<ExpressBillExceptionReport> recordList = expressBillExceptionReportDao.newQueryList(query);
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
        vo.setReportTimeFormat(DateUtil.format(expressBillExceptionReport.getReportTime(), DateUtil.FORMAT_DATE_TIME));
        List<String> imgUrlList = new ArrayList<>();
        if(StringUtils.isNotBlank(vo.getReportImgUrls())){
            imgUrlList = JSON.parseArray(vo.getReportImgUrls(), String.class);
        }
        vo.setReportImgUrlList(imgUrlList);
        return  vo;
    }

    /**
     * 检查、拼接查询参数
     * @param query
     * @return
     */
    private Response<Boolean> checkParam(ExpressBillExceptionReportQuery query) {
        Response<Boolean> response = new Response<>();
        response.toSucceed();
        if(StringUtils.isNotEmpty(query.getPackageCode())&&!WaybillUtil.isPackageCode(query.getPackageCode())){
            response.toError("请正确输入包裹号");
            return response;
        }

        // 查询时间字段转化
        if(StringUtils.isNotEmpty(query.getQueryStartTimeStr())){
            query.setQueryStartTime(DateUtil.parse(query.getQueryStartTimeStr(), DateUtil.FORMAT_DATE_TIME));
        }
        if(StringUtils.isNotEmpty(query.getQueryEndTimeStr())){
            query.setQueryEndTime(DateUtil.parse(query.getQueryEndTimeStr(), DateUtil.FORMAT_DATE_TIME));
        }
        return  response;
    }

    /**
     * 获取面单举报类型
     * @return
     */
    @Override
    public Response<Map<Integer,String>> getAllExceptionReportType(){
        Response<Map<Integer,String>> response = new Response<>();
        JdCResponse<Map<Integer,String>>  jdCResponse = expressBillExceptionReportService.getAllExceptionReportType();
        if(jdCResponse.getData()!=null&&jdCResponse!=null){
            response.toSucceed();
            response.setData(jdCResponse.getData());
        }else {
            response.toError();
        }
        return response;
    }

    @Override
    public Response<List<String>> getPicUrlsById(Integer id) {
        Response<List<String>> response = new Response<>();
        if(id == null){
            response.toError("参数错误!");
            return response;
        }
        response.toSucceed();
        response.setData(expressBillExceptionReportService.getPicUrlsById(id));
        return response;
    }

    /**
     * 获取一级举报类型--分拣工作台使用
     */
    @Override
    public Response<Map<Integer, String>> getFirstReportType() {
        Response<Map<Integer,String>> response = new Response<>();
        response.toSucceed();
        List<FaceFirstAbnormalType> firstAbnormalTypeList = expressBillExceptionReportService.getFirstAbnormalType();
        if(CollectionUtils.isEmpty(firstAbnormalTypeList)){
            response.toError("未获取到面单举报一级原因,请联系分拣小秘!");
            return response;
        }
        Map<Integer, String> map = new HashMap<>();
        for(FaceFirstAbnormalType abnormalType : firstAbnormalTypeList){
            map.put(abnormalType.getAbnormalCode(), abnormalType.getAbnormalName());
        }
        response.setData(map);
        return response;
    }

    /**
     * 获取二级举报类型--分拣工作台使用
     * @param firstAbnormalType 一级举报类型
     */
    @Override
    public Response<Map<Integer, String>> getSecondReportType(Integer firstAbnormalType) {
        Response<Map<Integer,String>> response = new Response<>();
        response.toSucceed();
        if(firstAbnormalType == null){
            response.toError("一级举报类型不存在，请联系分拣小秘!");
            return response;
        }
        List<FaceSecondAbnormalType> secondAbnormalTypeList = expressBillExceptionReportService.getSecondAbnormalType(firstAbnormalType);
        if(CollectionUtils.isEmpty(secondAbnormalTypeList)){
            response.toError("未获取到面单举报一级原因,请联系分拣小秘!");
        }
        Map<Integer, String> map = new HashMap<>();
        for(FaceSecondAbnormalType abnormalType : secondAbnormalTypeList){
            map.put(abnormalType.getAbnormalCode(), abnormalType.getAbnormalName());
        }
        response.setData(map);
        return response;
    }
}
    
