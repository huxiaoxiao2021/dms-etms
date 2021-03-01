package com.jd.bluedragon.distribution.exceptionReport.billException.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.FirstSiteVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.reponse.ReportTypeVo;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.DmsBaseDict;
import com.jd.bluedragon.distribution.base.service.DmsBaseDictService;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.alibaba.fastjson.JSON;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: liming522
 * @Description: 面单异常举报
 * @Date: create in 2020/12/21 14:44
 */
@Service("expressBillExceptionReportService")
public class ExpressBillExceptionReportServiceImpl implements ExpressBillExceptionReportService {
    private static final Logger log  =  LoggerFactory.getLogger(ExpressBillExceptionReportServiceImpl.class);

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private ExpressBillExceptionReportDao expressBillExceptionReportDao;

    @Autowired
    private DmsBaseDictService dmsBaseDictService;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    /**
     * 面单异常提交
     * @param reportRequest
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.reportExpressBillException", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Boolean> reportExpressBillException(ExpressBillExceptionReportRequest reportRequest) {
        JdCResponse<Boolean> result = new JdCResponse<>();
        if(log.isInfoEnabled()){
            log.info("ExpressBillExceptionReportServiceImpl.reportExpressBillException param {}", JSON.toJSONString(reportRequest));
        }

        try {
            //1.先校验参数
            if(!WaybillUtil.isPackageCode(reportRequest.getPackageCode())){
                result.toFail("举报失败,包裹号格式不正确");
                result.setData(false);
                return result;
            }

            //2.校验该包裹是否已经举报过
            if(this.selectPackageIsReport(reportRequest.getPackageCode())){
                result.toFail("举报失败,该包裹已被举报过");
                result.setData(false);
                return result;
            }

            //封装记录
            ExpressBillExceptionReport record = this.assembleRecord(reportRequest);

            //封装商城订单号
            this.assembleWayBillOrderId(reportRequest.getPackageCode(),record);

            //3.数据增加
            expressBillExceptionReportDao.insertReport(record);
            result.toSucceed("举报成功");
            result.setData(true);
        }catch (Exception e){
            log.error("面单举报异常 reportRequest:{}",JSON.toJSONString(reportRequest),e);
            result.toFail("举报失败,面单举报服务异常");
            result.setData(false);
        }
        return result;
    }

    /**
     * 活动包裹订单号
     * @param packageCode
     */
    private void assembleWayBillOrderId(String packageCode,ExpressBillExceptionReport record) {
        String waybillCode = WaybillUtil.getWaybillCode(packageCode);
        String orderId =  waybillQueryManager.getOrderCodeByWaybillCode(waybillCode,true);
        if(StringUtils.isNotEmpty(orderId)){
            record.setOrderId(orderId);
        }else {
            log.warn("面单异常举报获取运单订单号为空 waybillCode:{}",waybillCode);
        }
    }

    /**
     * 获取包裹始发地
     * @param packageCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getFirstSiteByPackageCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<FirstSiteVo> getFirstSiteByPackageCode(String packageCode) {
        JdCResponse<FirstSiteVo> result =  new  JdCResponse<>();
        result.toSucceed();
        if(log.isInfoEnabled()){
            log.info("ExpressBillExceptionReportServiceImpl.getFirstSiteByPackageCode param {}", packageCode);
        }
        //1.调用接口获取运单是否站点
        try {
            if(!WaybillUtil.isPackageCode(packageCode)){
                result.toFail("包裹号格式不正确");
                return result;
            }

            //始发地对象
            FirstSiteVo firstSiteVo = null;
            // 获取运单始发地
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);

            //1.先找出仓配的始发地
            Waybill baseEntity =  waybillQueryManager.getWaybillByWayCode(waybillCode);
            if(baseEntity.getDistributeStoreId()!=null && StringUtils.isNotEmpty(baseEntity.getDistributeStoreName())){
                firstSiteVo =  this.packageFirstSiteVo(baseEntity.getDistributeStoreId(),baseEntity.getDistributeStoreName());
                result.setData(firstSiteVo);
                return result;
            }


            //2.找揽收完成的--满足纯配营业部、驻场、车队
            List<PackageStateDto>   stateList  = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            if(CollectionUtils.isNotEmpty(stateList)){
                PackageStateDto packageState = stateList.get(0);
                firstSiteVo = this.packageFirstSiteVo(packageState.getOperatorSiteId(),packageState.getOperatorSite());
                result.setData(firstSiteVo);
                return result;
            }

            //3.找换单完成的-换单营业部
            List<PackageStateDto>   changeStateList  = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_EXCHANGE);

            if(CollectionUtils.isEmpty(changeStateList)){
                log.error("当前包裹{}，无法获取始发地",packageCode);
                result.toFail("当前包裹，无法获取始发地");
                return result;
            }

            PackageStateDto packageState = changeStateList.get(0);
            firstSiteVo = this.packageFirstSiteVo(packageState.getOperatorSiteId(),packageState.getOperatorSite());
            result.setData(firstSiteVo);
        }catch (Exception e){
            log.error("通过包裹号获取运单始发网点异常 packageCode:{}",packageCode,e);
            result.toFail("始发网点获取异常");
        }
        return result;
    }

    /**
     * 封装始发站点对象
     * @param siteCode
     * @param siteName
     * @return
     */
    private FirstSiteVo packageFirstSiteVo(Integer siteCode,String siteName){
        FirstSiteVo firstSiteVo = new FirstSiteVo();
        firstSiteVo.setFirstSiteName(siteName);
        firstSiteVo.setFirstSiteCode(siteCode);
        return firstSiteVo;
    }

    /**
     * 获取异常举报类型枚举值
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getAllExceptionReportType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<Map<Integer,String>> getAllExceptionReportType() {
        JdCResponse<Map<Integer,String>> result =  new  JdCResponse<>();
        result.toSucceed();
        result.setData(getDictMap());
        return result;
    }

    private Map<Integer,String > getDictMap(){
        List<DmsBaseDict> list = dmsBaseDictService.queryListByParentId(Constants.EXPRESS_BILL_REPORT_PARENT_ID);
        Map<Integer, String> map = new HashMap<Integer, String>();
        for (int i = 0; i < list.size(); i++) {
            map.put(list.get(i).getTypeCode(), list.get(i).getMemo());
        }
        return map;
    }

    /**
     * 提供给安卓的举报类型枚举方法
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getAllExceptionReportTypeList", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpressBillExceptionReportTypeEnum>> getAllExceptionReportTypeList() {
        JdCResponse<List<ExpressBillExceptionReportTypeEnum>> result =  new  JdCResponse<>();
        result.toSucceed();
        List<ExpressBillExceptionReportTypeEnum> allTypes = ExpressBillExceptionReportTypeEnum.getAllExpressBillExceptionReportType();
        result.setData(allTypes);
        return result;
    }

    /**
     * 提供给安卓的新方法(举报类型)
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getAllExceptionReportTypeListNew", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ReportTypeVo>> getAllExceptionReportTypeListNew() {
        JdCResponse<List<ReportTypeVo>> result =  new  JdCResponse<>();
        result.toSucceed();
        List<ReportTypeVo> list = new ArrayList<>();
        Map<Integer,String> dictMap = getDictMap();
        for (Map.Entry<Integer, String>  map : dictMap.entrySet()){
            ReportTypeVo reportTypeVo = new ReportTypeVo();
            reportTypeVo.setReportTypeCode(map.getKey());
            reportTypeVo.setReportTypeName(map.getValue());
            list.add(reportTypeVo);
        }
        result.setData(list);
        return result;
    }

    /**
     * 判断包裹是否举报过
     * @param packageCode
     * @return  true:举报过   false: 未举报过
     */
    @Override
    public boolean selectPackageIsReport(String packageCode) {
        int num = expressBillExceptionReportDao.selectPackageIsReport(packageCode);
        if(num>0){
            return  true;
        }
        return false;
    }

    /**
     * 组装插入数据
     * @param reportRequest
     * @return
     */
    private ExpressBillExceptionReport assembleRecord(ExpressBillExceptionReportRequest reportRequest) {
        ExpressBillExceptionReport report = new ExpressBillExceptionReport();
        report.setPackageCode(reportRequest.getPackageCode());
        report.setOrgCode(reportRequest.getCurrentOperate().getOrgId());
        report.setOrgName(reportRequest.getCurrentOperate().getOrgName());
        report.setSiteCode(reportRequest.getCurrentOperate().getSiteCode());
        report.setSiteName(reportRequest.getCurrentOperate().getSiteName());
        report.setFirstSiteCode(reportRequest.getFirstSiteCode());
        report.setFirstSiteName(reportRequest.getFirstSiteName());
        report.setReportImgUrls(reportRequest.getReportPictureUrls());
        report.setReportTime(reportRequest.getReportTime());
        report.setReportType(reportRequest.getReportType());
        report.setReportTypeName(reportRequest.getReportTypeName());
        report.setReportUserErp(reportRequest.getUser().getUserErp());
        report.setCreateTime(new Date());
        report.setUpdateTime(new Date());
        report.setYn(1);
        report.setTs(new Date());
        report.setRemark(reportRequest.getRemark());
        return report;
    }
}
    
