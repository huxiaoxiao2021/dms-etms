package com.jd.bluedragon.distribution.exceptionReport.billException.service.impl;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.request.CurrentOperate;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.Enum.ExpressBillExceptionReportTypeEnum;
import com.jd.bluedragon.common.dto.exceptionReport.expressBill.request.ExpressBillExceptionReportRequest;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.exceptionReport.billException.dao.ExpressBillExceptionReportDao;
import com.jd.bluedragon.distribution.exceptionReport.billException.domain.ExpressBillExceptionReport;
import com.jd.bluedragon.distribution.exceptionReport.billException.service.ExpressBillExceptionReportPdaService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.etms.waybill.dto.PackageStateDto;
import com.jd.fastjson.JSON;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @Author: liming522
 * @Description: 面单异常举报
 * @Date: create in 2020/12/21 14:44
 */
@Service("expressBillExceptionReportPdaService")
public class ExpressBillExceptionReportPdaServiceImpl implements ExpressBillExceptionReportPdaService {
    private static final Logger log  =  LoggerFactory.getLogger(ExpressBillExceptionReportPdaServiceImpl.class);

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    @Autowired
    private ExpressBillExceptionReportDao expressBillExceptionReportDao;

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
                result.toFail("包裹号格式不正确");
                result.setData(false);
                return result;
            }

            //2.校验该包裹是否已经举报过
            if(this.selectPackageIsReport(reportRequest.getPackageCode())){
                result.toFail("该包裹已被举报过");
                result.setData(false);
                return result;
            }

            //封装记录
            ExpressBillExceptionReport record = this.assembleRecord(reportRequest);
            //3.数据增加
            expressBillExceptionReportDao.insertReport(record);
            result.toSucceed("举报成功");
            result.setData(true);
        }catch (Exception e){
            log.error("面单举报异常 reportRequest:{}",JSON.toJSONString(reportRequest),e);
            result.toFail("面单举报异常");
            result.setData(false);
        }
        return result;
    }

    /**
     * 获取包裹始发地
     * @param packageCode
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getFirstSiteByPackageCode", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<CurrentOperate> getFirstSiteByPackageCode(String packageCode) {
        JdCResponse<CurrentOperate> result =  new  JdCResponse<>();
        if(log.isInfoEnabled()){
            log.info("ExpressBillExceptionReportServiceImpl.getFirstSiteByPackageCode param {}", packageCode);
        }
        //1.调用接口获取运单是否站点
        try {
            if(!WaybillUtil.isPackageCode(packageCode)){
                result.toFail("包裹号格式不正确");
                return result;
            }
            // 获取运单始发地
            String waybillCode = WaybillUtil.getWaybillCode(packageCode);
            List<PackageStateDto>   stateList  = waybillTraceManager.getPkStateDtoByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_COLLECT_COMPLETE);
            if(CollectionUtils.isEmpty(stateList)){
                result.toFail("当前包裹没揽收完成，无法获取始发地");
                return result;
            }
            result.toSucceed();
            PackageStateDto packageState = stateList.get(0);
            CurrentOperate  reportExceptionDto = new CurrentOperate();
            reportExceptionDto.setSiteName(packageState.getOperatorSite());
            reportExceptionDto.setSiteCode(packageState.getOperatorSiteId());

            result.setData(reportExceptionDto);
        }catch (Exception e){
            log.error("通过包裹号获取运单始发网点异常 packageCode:{}",packageCode,e);
            result.toFail("始发网点获取异常");
        }
        return result;
    }

    /**
     * 获取异常举报类型枚举值
     * @return
     */
    @Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "DMSWEB.ExpressBillExceptionReportServiceImpl.getAllExceptionReportType", mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdCResponse<List<ExpressBillExceptionReportTypeEnum>> getAllExceptionReportType() {
        JdCResponse<List<ExpressBillExceptionReportTypeEnum>> result =  new  JdCResponse<>();
        result.toSucceed();
        List<ExpressBillExceptionReportTypeEnum> allTypes = ExpressBillExceptionReportTypeEnum.getAllExpressBillExceptionReportType();
        result.setData(allTypes);
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
        report.setFirstSiteCode(report.getFirstSiteCode());
        report.setFirstSiteName(report.getFirstSiteName());
        report.setReportImgUrls(reportRequest.getReportPictureUrls());
        report.setReportTime(reportRequest.getReportTime());
        report.setReportType(reportRequest.getReportType());
        report.setReportTypeName(reportRequest.getReportTypeName());
        report.setReportUserErp(reportRequest.getUser().getUserErp());
        report.setYn(1);
        report.setTs(new Date());
        return report;
    }
}
    
