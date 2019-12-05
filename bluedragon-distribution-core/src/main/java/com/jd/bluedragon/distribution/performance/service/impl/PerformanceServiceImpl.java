package com.jd.bluedragon.distribution.performance.service.impl;

import com.jd.b2b.wt.assemble.sdk.req.HandoverBillPrintReq;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverBillResp;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverDetailResp;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.core.base.HandoverBillPrintManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.performance.domain.Commodity;
import com.jd.bluedragon.distribution.performance.domain.Performance;
import com.jd.bluedragon.distribution.performance.domain.PerformanceCondition;
import com.jd.bluedragon.distribution.performance.service.PerformanceService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: PerformanceServiceImpl
 * @Description: 123
 * @author: hujiping
 * @date: 2018/8/20 13:45
 */
@Service("performanceService")
public class PerformanceServiceImpl implements PerformanceService {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private HandoverBillPrintManager handoverBillPrintManager;

    @Autowired
    private WaybillCommonService waybillCommonService;

    /**
     * 查询加履系统获得信息
     * @param performanceCondition
     * @return
     */
    @Override
    public PagerResult<Performance> listData(PerformanceCondition performanceCondition) {
        PagerResult<Performance> pagerResult = new PagerResult<Performance>();
        List<Performance> list = new ArrayList<Performance>();
        pagerResult.setRows(list);
        pagerResult.setTotal(0);

        HandoverBillPrintReq handoverBillPrintReq = new HandoverBillPrintReq();
        String performanceCode = performanceCondition.getPerformanceCode();
        if(StringUtils.isEmpty(performanceCondition.getPerformanceCode()) &&
                StringUtils.isEmpty(performanceCondition.getWaybillorPackCode())) {
            return pagerResult;
        }
        //调运单接口查询履约单号
        if(StringUtils.isEmpty(performanceCondition.getPerformanceCode()) ||
                StringUtils.isEmpty(performanceCondition.getPerformanceCode().trim())){
            String waybillCode = performanceCondition.getWaybillorPackCode();
            if(WaybillUtil.isPackageCode(performanceCondition.getWaybillorPackCode())){
                waybillCode = WaybillUtil.getWaybillCode(performanceCondition.getWaybillorPackCode());
            }
            try {
                performanceCode = waybillCommonService.getPerformanceCode(waybillCode);
                if(performanceCode == null){
                    this.log.warn("通过运单号{}获得履约单号失败",waybillCode);
                    return pagerResult;
                }
                handoverBillPrintReq.setFulfillmentOrderId(performanceCode);
            }catch (Exception e){
                this.log.error("通过运单号获得履约单号失败:{}" , waybillCode,e);
            }

        }else {
            handoverBillPrintReq.setFulfillmentOrderId(performanceCondition.getPerformanceCode());
        }
        handoverBillPrintReq.setPageNo(performanceCondition.getOffset()/performanceCondition.getLimit()+1);
        handoverBillPrintReq.setPageSize(performanceCondition.getLimit());
        try {
            HandoverBillResp handoverBillResp = handoverBillPrintManager.searchHandoverDetail(handoverBillPrintReq);
            if (handoverBillResp != null) {
                List<HandoverDetailResp> respsList = handoverBillResp.getHandoverDetailResps();
                for (HandoverDetailResp handoverDetailResp : respsList) {
                    Performance performance = new Performance();
                    performance.setPerformanceCode(performanceCode);
                    performance.setPoNo(handoverDetailResp.getPoNo());
                    performance.setWaybillCode(handoverDetailResp.getDeliveryOrderId());
                    performance.setGoodName(handoverDetailResp.getSkuName());
                    performance.setGoodNumber(handoverDetailResp.getSkuNum());
                    performance.setPackageCode(handoverDetailResp.getContainerId());
                    list.add(performance);
                }
                pagerResult.setTotal(handoverBillResp.getCountDetail());
                pagerResult.setRows(list);
            }else {
                this.log.warn("通过履约单号查询履约单信息失败:{}" , performanceCode);
            }
        }catch (Exception e){
            this.log.error("通过履约单号查询履约单信息失败:{}" , performanceCode,e);
        }

        return pagerResult;
    }

    /**
     * 组装履约单信息返回给前台
     * @param performanceCode
     */
    @Override
    public String print(String performanceCode,String waybillorPackCode) {

        InvokeResult<String> invokeResult = new InvokeResult<String>();
        invokeResult.customMessage(400,"打印失败");

        HandoverBillPrintReq handoverBillPrintReq = new HandoverBillPrintReq();
        if(StringUtils.isEmpty(performanceCode) || StringUtils.isEmpty(performanceCode.trim())) {
            String waybillCode = waybillorPackCode;
            if (WaybillUtil.isPackageCode(waybillorPackCode)) {
                waybillCode = WaybillUtil.getWaybillCode(waybillorPackCode);
            }
            try {
                performanceCode = waybillCommonService.getPerformanceCode(waybillCode);
                if(performanceCode == null){
                    this.log.warn("通过运单号{}获得履约单号为空",waybillCode);
                    return JsonHelper.toJson(invokeResult);
                }
            } catch (Exception e) {
                this.log.error("通过运单号获得运单信息失败:{}" , waybillCode, e);
                return JsonHelper.toJson(invokeResult);
            }
        }


        handoverBillPrintReq.setFulfillmentOrderId(performanceCode);
        List<HandoverDetailResp> handoverDetailRespList = handoverBillPrintManager.dismantlePrint(handoverBillPrintReq);
        if(handoverDetailRespList != null){

            //-----------------封装数据start-----------------
            //key:箱号，value:该箱号下所有的商品
            Map<String,List<Commodity>> boxMap = new LinkedHashMap<String, List<Commodity>>();
            //key:运单号，value:该运单号下所有箱号
            Map<String,Map<String,List<Commodity>>> waybillMap = new LinkedHashMap<String,Map<String,List<Commodity>>>();
            //key:履约单号，value:该履约单号下所有运单号
            Map<String,Map<String,Map<String,List<Commodity>>>> performanceMap = new LinkedHashMap<String,Map<String,Map<String,List<Commodity>>>>();
            for(HandoverDetailResp resp : handoverDetailRespList){
                Commodity commodity = new Commodity();
                commodity.setPoNo(resp.getPoNo());
                commodity.setSkuNum(resp.getSkuNum());
                commodity.setSkuName(resp.getSkuName());
                commodity.setSkuId(resp.getSkuId());
                commodity.setBoxCode(resp.getContainerId());
                commodity.setWaybillCode(resp.getDeliveryOrderId());
                commodity.setMap(resp.getOrderMap());
                //箱号不包含就添加进去
                if(!boxMap.containsKey(resp.getContainerId())){
                    List<Commodity> skuList =new ArrayList<Commodity>();
                    skuList.add(commodity);
                    boxMap.put(resp.getContainerId(),skuList);
                }else {//包含就将对象添加到list中
                    List<Commodity> skuList = boxMap.get(resp.getContainerId());
                    skuList.add(commodity);
                    boxMap.put(resp.getContainerId(),skuList);
                }
            }
            for(String boxCode : boxMap.keySet()){
                //运单号不包含就添加进去
                if(!waybillMap.containsKey(boxMap.get(boxCode).get(0).getWaybillCode())){
                    Map<String,List<Commodity>> newBoxMap = new HashMap<String, List<Commodity>>();
                    newBoxMap.put(boxCode,boxMap.get(boxCode));
                    waybillMap.put(boxMap.get(boxCode).get(0).getWaybillCode(),newBoxMap);
                }else{
                    Map<String, List<Commodity>> map = waybillMap.get(boxMap.get(boxCode).get(0).getWaybillCode());
                    map.put(boxCode, boxMap.get(boxCode));
                    waybillMap.put(boxMap.get(boxCode).get(0).getWaybillCode(),map);
                }
            }
            //-----------------封装数据end-----------------

            performanceMap.put(performanceCode,waybillMap);
            invokeResult.success();
            invokeResult.setData(JsonHelper.toJson(performanceMap));
        }
        return JsonHelper.toJson(invokeResult);
    }

    /**
     * 判断是否可以打印
     * @param performanceCondition
     * @return
     */
    @Override
    public Integer getIsPrint(PerformanceCondition performanceCondition) {


        HandoverBillPrintReq handoverBillPrintReq = new HandoverBillPrintReq();
        String performanceCode = performanceCondition.getPerformanceCode();
        if(StringUtils.isEmpty(performanceCondition.getPerformanceCode()) &&
                StringUtils.isEmpty(performanceCondition.getWaybillorPackCode())) {
            return 3;
        }
        //调运单接口查询履约单号
        if(StringUtils.isEmpty(performanceCondition.getPerformanceCode()) ||
                StringUtils.isEmpty(performanceCondition.getPerformanceCode().trim())){
            String waybillCode = performanceCondition.getWaybillorPackCode();
            if(WaybillUtil.isPackageCode(performanceCondition.getWaybillorPackCode())){
                waybillCode = WaybillUtil.getWaybillCode(performanceCondition.getWaybillorPackCode());
            }
            try {
                performanceCode = waybillCommonService.getPerformanceCode(waybillCode);
                if(performanceCode == null){
                    this.log.warn("通过运单号{}获得履约单号失败",waybillCode);
                    return 3;
                }
                handoverBillPrintReq.setFulfillmentOrderId(performanceCode);
            }catch (Exception e){
                this.log.error("通过运单号获得履约单号失败:{}" , waybillCode,e);
            }

        }else {
            handoverBillPrintReq.setFulfillmentOrderId(performanceCondition.getPerformanceCode());
        }
        handoverBillPrintReq.setPageNo(1);
        handoverBillPrintReq.setPageSize(10);
        try {
            if(handoverBillPrintManager.searchHandoverisCanPrint(handoverBillPrintReq)) {
                return 1;
            }
            return 2;
        }catch (Exception e){
            this.log.error("通过运单号获得打印信息失败:{}" , performanceCode,e);
        }
        return 3;
    }

}
