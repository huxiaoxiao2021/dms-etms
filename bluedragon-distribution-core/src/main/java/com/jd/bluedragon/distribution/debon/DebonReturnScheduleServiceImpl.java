package com.jd.bluedragon.distribution.debon;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.common.dto.base.response.JdCResponse;
import com.jd.bluedragon.core.base.ExpressDispatchServiceManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.request.WaybillForPreSortOnSiteRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.reassignWaybill.domain.ReassignWaybill;
import com.jd.bluedragon.distribution.reassignWaybill.service.ReassignWaybillService;
import com.jd.bluedragon.distribution.waybill.service.WaybillService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.preseparate.vo.B2bVehicleTeamMatchRequest;
import com.jd.preseparate.vo.B2bVehicleTeamMatchResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
@Service("debonService")
public class DebonReturnScheduleServiceImpl implements DebonReturnScheduleService {

    private static final Logger log = LoggerFactory.getLogger(DebonReturnScheduleServiceImpl.class);
    @Autowired
    private WaybillQueryManager waybillQueryManager;
    @Autowired
    private ExpressDispatchServiceManager expressDispatchServiceManager;

    @Autowired
    private WaybillService waybillService;
    @Autowired
    private ReassignWaybillService reassignWaybill;

    @Override
    public JdCResponse<Boolean> returnScheduleSiteInfoByWaybill(ReturnScheduleRequest request) {
        if (log.isInfoEnabled()) {
            log.info("DebangServiceImpl.getMatchSiteInfoByWaybill-入参-{}", JSON.toJSONString(request));
        }
        JdCResponse<Boolean> response = new JdCResponse<>();
        response.setData(Boolean.TRUE);
        if(!checkParam(response,request)){
            return response;
        }
        String waybillCode = request.getWaybillCode();
        WaybillUtil.getWaybillCode(waybillCode);
        if (StringUtils.isBlank(waybillCode)) {
            log.error("运单信息为空!");
            response.setData(Boolean.FALSE);
            response.toFail("运单信息不能为空!");
            return response;
        }
        //根据运单获取运单信息
        BaseEntity<BigWaybillDto> dataByChoice
                = waybillQueryManager.getDataByChoice(waybillCode, true, true, true, true);
        if (dataByChoice == null
                || dataByChoice.getData() == null
                || dataByChoice.getData().getWaybill() == null
                || CollectionUtils.isEmpty(dataByChoice.getData().getPackageList())
                || StringUtils.isBlank(dataByChoice.getData().getWaybill().getReceiverAddress())) {
            log.warn("查询运单失败!-{}", waybillCode);
            response.setData(Boolean.FALSE);
            response.toFail("获取运单信息失败!");
            return response;
        }

        List<DeliveryPackageD> packageList = dataByChoice.getData().getPackageList();
        List<String> packges = packageList.stream().map(DeliveryPackageD::getPackageBarcode).collect(Collectors.toList());

        Waybill waybill = dataByChoice.getData().getWaybill();
        B2bVehicleTeamMatchRequest matchRequest = new B2bVehicleTeamMatchRequest();
        matchRequest.setOrderId(waybillCode);
        //需确认 订单类型是不是运单类型？？？？？？
        matchRequest.setOrderType(waybill.getWaybillType());
        matchRequest.setProvinceId(waybill.getProvinceId());
        matchRequest.setCityId(waybill.getCityId());
        matchRequest.setAddress(waybill.getReceiverAddress());
        matchRequest.setWeight(new BigDecimal(waybill.getGoodWeight()));
        matchRequest.setVolume(new BigDecimal(waybill.getGoodVolume()));
        //获取匹配站点信息
        B2bVehicleTeamMatchResult matchResult = expressDispatchServiceManager.getStandardB2bSupportMatch(matchRequest);
        if (matchResult == null || !matchResult.getB2bSupport()) {
            log.warn("匹配站点信息失败!-{}", waybillCode);
            response.setData(Boolean.FALSE);
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
        InvokeResult<String> invokeResult = waybillService.checkWaybillForPreSortOnSite(preSortOnSiteRequest);
        log.info("调用返调度之前校验 出参 -{}",JSON.toJSONString(invokeResult));
        //如果校验失败直接返回
        if(InvokeResult.RESULT_SUCCESS_CODE != invokeResult.getCode()){
            response.setData(Boolean.FALSE);
            response.toFail(invokeResult.getMessage());
            return response;
        }

        List<ReassignWaybill> reassignWaybills = coverToReassignWaybill(request, waybill, matchResult, packges);


        if(!CollectionUtils.isEmpty(reassignWaybills)) {
            for(ReassignWaybill reassignWaybillDto: reassignWaybills) {
                reassignWaybill.add(reassignWaybillDto);
            }
        }
        response.setMessage(JdCResponse.MESSAGE_SUCCESS);
        return response;
    }

    /**
     * 入参校验
     * @param response
     * @param request
     * @return
     */
    private boolean checkParam(JdCResponse<Boolean> response, ReturnScheduleRequest request) {
        if (request == null || request.getRequestProfile() == null || request.getOperatorInfo() == null) {
            response.fail("入参不能为空!");
            return false;
        }
        if (StringUtils.isBlank(request.getOperatorInfo().getOperateUserErp()) || request.getOperatorInfo().getOperateSiteId() == null) {
            response.fail("操作信息不能为空!");
            return false;
        }
        if (StringUtils.isBlank(request.getWaybillCode())) {
            response.fail("运单信息不能为空!");
            return false;
        }
        return true;
    }

    /**
     * 封装 ReassignWaybill
     * @param request
     * @param waybill
     * @param matchResult
     * @return
     */
    public List<ReassignWaybill> coverToReassignWaybill(ReturnScheduleRequest request,Waybill waybill
            ,B2bVehicleTeamMatchResult matchResult,List<String> packges){

        List<ReassignWaybill> reassignWaybills = new ArrayList<>();
        for (String packge : packges) {
            ReassignWaybill reassignWaybill = new ReassignWaybill();
            reassignWaybill.setInterfaceType(RESCHEDULE_PRINT.getType());
            reassignWaybill.setWaybillCode(request.getWaybillCode());
            reassignWaybill.setPackageBarcode(packge);
            reassignWaybill.setAddress(waybill.getReceiverAddress());
            reassignWaybill.setSiteCode(request.getOperatorInfo().getOperateSiteId());
            reassignWaybill.setSiteName(request.getOperatorInfo().getOperateSiteName());
//        reassignWaybill.setReceiveSiteCode();
//        reassignWaybill.setReceiveSiteName();
            reassignWaybill.setChangeSiteCode(matchResult.getVehicleTeamId());
            reassignWaybill.setChangeSiteName(matchResult.getVehicleTeamName());
            reassignWaybill.setUserCode(request.getOperatorInfo().getOperateUserId());
            reassignWaybill.setUserName(request.getOperatorInfo().getOperateUserName());
            Long operateTime = request.getOperatorInfo().getOperateTime();
            reassignWaybill.setOperateTime(operateTime == null ?new Date() :DateHelper.parseDateTime(operateTime.toString()));
            reassignWaybills.add(reassignWaybill);
        }
        return reassignWaybills;
    }
}
