package com.jd.bluedragon.distribution.debon;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.ExpressDispatchServiceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.debon.Dto.ReturnScheduleMq;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleRequest;
import com.jd.bluedragon.distribution.jsf.domain.ReturnScheduleResult;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.domain.WaybillManageDomain;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.preseparate.vo.B2bVehicleTeamMatchRequest;
import com.jd.preseparate.vo.B2bVehicleTeamMatchResult;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.dms.common.domain.JdResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum.RESCHEDULE_PRINT;

/**
 * @Author: chenyaguo@jd.com
 * @Date: 2023/5/19 17:23
 * @Description:
 */
@Service("debonReturnScheduleService")
public class WaybillReturnScheduleServiceImpl implements WaybillReturnScheduleService {

    private static final Logger log = LoggerFactory.getLogger(WaybillReturnScheduleServiceImpl.class);
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private ExpressDispatchServiceManager expressDispatchServiceManager;

    @Autowired
    private WaybillService waybillService;

    @Autowired
    @Qualifier("debonReturnScheduleProducer")
    private DefaultJMQProducer debonReturnScheduleProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;

    @Override
    @JProfiler(jKey = "DMSWEB.WaybillReturnScheduleServiceImpl.returnScheduleSiteInfoByWaybill",jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public JdResponse<ReturnScheduleResult> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request) {
        if (log.isInfoEnabled()) {
            log.info("DebangServiceImpl.getMatchSiteInfoByWaybill-入参-{}", JSON.toJSONString(request));
        }
        JdResponse<ReturnScheduleResult> response = new JdResponse<>();
        response.toSucceed(JdCResponse.MESSAGE_SUCCESS);

        if(!checkParam(response,request)){
            return response;
        }
        String waybillCode = request.getWaybillCode();
        //根据运单号获取运单信息
        BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, true);
        log.info("dataByChoice-{}",JSON.toJSONString(dataByChoice));
        if (dataByChoice == null
                || dataByChoice.getData() == null
                || dataByChoice.getData().getWaybill() == null
                || StringUtils.isBlank(dataByChoice.getData().getWaybill().getWaybillSign())
                || dataByChoice.getData().getWaybillState() == null
                || CollectionUtils.isEmpty(dataByChoice.getData().getPackageList())
                || StringUtils.isBlank(dataByChoice.getData().getWaybill().getReceiverAddress())) {
            log.warn("查询运单失败!-{}", waybillCode);
            response.toFail("获取运单信息失败!");
            return response;
        }

        Waybill waybill = dataByChoice.getData().getWaybill();
        B2bVehicleTeamMatchRequest matchRequest = coverB2bVehicleTeamMatchRequest(waybill, waybillCode);
        //获取匹配站点信息
        B2bVehicleTeamMatchResult matchResult = expressDispatchServiceManager.getStandardB2bSupportMatch(matchRequest);
        if (matchResult == null || !matchResult.getB2bSupport()) {
            log.warn("匹配站点信息失败!-{}", waybillCode);
            response.toFail("匹配站点信息失败!");
            return response;
        }

        //检验返调度站点信息
        WaybillForPreSortOnSiteRequest preSortOnSiteRequest = new WaybillForPreSortOnSiteRequest();
        preSortOnSiteRequest.setWaybill(waybillCode);
        preSortOnSiteRequest.setErp(request.getOperatorInfo().getOperateUserErp());
        preSortOnSiteRequest.setSortingSite(request.getOperatorInfo().getOperateSiteId());
        preSortOnSiteRequest.setSiteOfSchedulingOnSite(matchResult.getVehicleTeamId());
        log.info("调用返调度之前校验 入参 -{}",JSON.toJSONString(preSortOnSiteRequest));
        InvokeResult<String> invokeResult = waybillService.checkWaybillForPreSortOnSiteToDebon(preSortOnSiteRequest,waybill);
        log.info("调用返调度之前校验 出参 -{}",JSON.toJSONString(invokeResult));
        //如果校验失败直接返回
        if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
            response.toFail(invokeResult.getMessage());
            return response;
        }
        WaybillManageDomain waybillState = dataByChoice.getData().getWaybillState();
        //获取包裹列表
        List<DeliveryPackageD> packageList = dataByChoice.getData().getPackageList();
        List<String> packges = packageList.stream().map(DeliveryPackageD::getPackageBarcode).collect(Collectors.toList());
        List<ReassignWaybill> reassignWaybills = coverToReassignWaybill(request, waybill,waybillState, matchResult, packges);

        if(!CollectionUtils.isEmpty(reassignWaybills)) {
            try {
                for(ReassignWaybill reassignWaybill: reassignWaybills) {
                    log.info("debonReturnScheduleProducer 发送德邦返调度信息-{}",JsonHelper.toJson(reassignWaybill));
                    debonReturnScheduleProducer.send(reassignWaybill.getPackageBarcode(), JsonHelper.toJson(reassignWaybill));
                }
            }catch (Exception e){
                log.error("returnScheduleSiteInfoByWaybill 发送德邦返调度信息失败",e);
            }
        }
        ReturnScheduleResult resultData = coverReturnScheduleSiteInfo(response, matchResult, waybillCode);
        response.setData(resultData);

        return response;
    }

    /**
     * 获取站点信息
     * @param response
     * @param matchResult
     * @param waybillCode
     * @return
     */
    private ReturnScheduleResult coverReturnScheduleSiteInfo(JdResponse<ReturnScheduleResult> response, B2bVehicleTeamMatchResult matchResult,String waybillCode){
        ReturnScheduleResult resultData = new ReturnScheduleResult();
        //查詢基础资料对匹配的返调度站点的siteCode重新赋值返回
        BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteBySiteId(matchResult.getVehicleTeamId());
        if(baseSite == null){
            response.toFail("获取调度站点基础信息失败!");
            return resultData;
        }
        resultData.setWaybillCode(waybillCode);
        resultData.setSiteCode(baseSite.getDmsSiteCode());
        resultData.setSiteName(matchResult.getVehicleTeamName());
      return resultData;
    }

    /**
     * 入参校验
     * @param response
     * @param request
     * @return
     */
    private boolean checkParam(JdResponse<ReturnScheduleResult> response, ReturnScheduleRequest request) {
        if (request == null || request.getOperatorInfo() == null) {
            response.toFail("入参不能为空!");
            return false;
        }
        if (StringUtils.isBlank(request.getOperatorInfo().getOperateSiteCode())) {
            response.toFail("操作场地信息不能为空!");
            return false;
        }
        if (StringUtils.isBlank(request.getWaybillCode())) {
            response.toFail("运单信息不能为空!");
            return false;
        }
        return true;
    }

    public B2bVehicleTeamMatchRequest coverB2bVehicleTeamMatchRequest(Waybill waybill,String waybillCode){
        B2bVehicleTeamMatchRequest matchRequest = new B2bVehicleTeamMatchRequest();

        String waybillSign = waybill.getWaybillSign();
        matchRequest.setOrderId(waybillCode);
        //判断自营单还是外单
        if(BusinessUtil.isSelf(waybillSign)){
            matchRequest.setOrderType(Constants.B2BSUPPORT_ORDER_TYPE_1);
        }else {
            matchRequest.setOrderType(Constants.B2BSUPPORT_ORDER_TYPE_2);
        }
        matchRequest.setProvinceId(waybill.getProvinceId());
        matchRequest.setCityId(waybill.getCityId());
        matchRequest.setAddress(waybill.getReceiverAddress());
        matchRequest.setWeight(new BigDecimal(waybill.getGoodWeight()));
        matchRequest.setVolume(new BigDecimal(waybill.getGoodVolume()));
        return matchRequest;
    }

    /**
     * 封装 ReassignWaybill
     * @param request
     * @param waybill
     * @param matchResult
     * @return
     */
    public List<ReassignWaybill> coverToReassignWaybill(ReturnScheduleRequest request,Waybill waybill,WaybillManageDomain waybillState
            ,B2bVehicleTeamMatchResult matchResult,List<String> packges){

        Integer siteCode= null;
        String siteName ="";
        if(StringUtils.isNotBlank(request.getOperatorInfo().getOperateSiteCode())){
            BaseStaffSiteOrgDto baseSite = baseMajorManager.getBaseSiteByDmsCode(request.getOperatorInfo().getOperateSiteCode());
            if(baseSite != null){
                siteCode = baseSite.getSiteCode();
                siteName = baseSite.getSiteName();
            }
        }
        List<ReassignWaybill> reassignWaybills = new ArrayList<>();
        for (String packge : packges) {
            ReassignWaybill reassignWaybill = new ReassignWaybill();
            reassignWaybill.setWaybillCode(request.getWaybillCode());
            reassignWaybill.setPackageBarcode(packge);
            reassignWaybill.setAddress(waybill.getReceiverAddress());
            reassignWaybill.setSiteCode(siteCode);
            reassignWaybill.setSiteName(siteName);
            reassignWaybill.setReceiveSiteCode(waybillState.getSiteId());
            reassignWaybill.setReceiveSiteName(waybillState.getSiteName());
            reassignWaybill.setChangeSiteCode(matchResult.getVehicleTeamId());
            reassignWaybill.setChangeSiteName(matchResult.getVehicleTeamName());
            reassignWaybill.setUserCode(1);
            reassignWaybill.setUserName("德邦运营人员");
            Long operateTime = request.getOperatorInfo().getOperateTime();
            reassignWaybill.setOperateTime(operateTime == null ?new Date() :DateHelper.toDate(operateTime));
            reassignWaybill.setInterfaceType(RESCHEDULE_PRINT.getType());
            reassignWaybills.add(reassignWaybill);
        }
        return reassignWaybills;
    }
}
