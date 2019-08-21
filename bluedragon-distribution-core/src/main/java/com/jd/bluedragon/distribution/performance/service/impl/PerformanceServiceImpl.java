package com.jd.bluedragon.distribution.performance.service.impl;

import com.jd.b2b.wt.assemble.sdk.RpcResult;
import com.jd.b2b.wt.assemble.sdk.req.HandoverBillPrintReq;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverBillResp;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverDetailResp;
import com.jd.b2b.wt.assemble.sdk.service.HandoverBillProvider;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.performance.domain.Commodity;
import com.jd.bluedragon.distribution.performance.domain.Performance;
import com.jd.bluedragon.distribution.performance.domain.PerformanceCondition;
import com.jd.bluedragon.distribution.performance.service.PerformanceService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.jp.assemble.sdk.GenericResult;
import com.jd.jp.assemble.sdk.HandoverBillResource;
import com.jd.jp.assemble.sdk.dto.HandoverBillDto;
import com.jd.jp.assemble.sdk.dto.HandoverBillPrint;
import com.jd.jp.assemble.sdk.dto.HandoverDetailDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PerformanceServiceImpl
 * @Description: 123
 * @author: hujiping
 * @date: 2018/8/20 13:45
 */
@Service("performanceService")
public class PerformanceServiceImpl implements PerformanceService {

    private final Logger logger = Logger.getLogger(this.getClass());

    //@Autowired
    //private HandoverBillResource handoverBillResource;
    @Autowired
    private HandoverBillProvider handoverBillProvider;
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
        //HandoverBillPrint handoverBillPrint = new HandoverBillPrint();
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
                    this.logger.error("通过运单号" + waybillCode + "获得履约单号失败");
                    return pagerResult;
                }
                handoverBillPrintReq.setFulfillmentOrderId(performanceCode);
            }catch (Exception e){
                this.logger.error("通过运单号获得履约单号失败" + waybillCode,e);
            }

        }else {
            handoverBillPrintReq.setFulfillmentOrderId(performanceCondition.getPerformanceCode());
        }
        handoverBillPrintReq.setPageNo(performanceCondition.getOffset()/performanceCondition.getLimit()+1);
        handoverBillPrintReq.setPageSize(performanceCondition.getLimit());
        try {
            RpcResult<HandoverBillResp> rpcResult = handoverBillProvider.searchDetail(handoverBillPrintReq);
            //HandoverBillDto handoverBillDto = handoverBillResource.searchDetail(handoverBillPrint);
            if (rpcResult != null && rpcResult.getSuccess()) {
                List<HandoverDetailResp> respsList = rpcResult.getValue().getHandoverDetailResps();
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
                pagerResult.setTotal(rpcResult.getValue().getCountDetail());
                pagerResult.setRows(list);
/*            if(handoverBillDto != null && handoverBillDto.getSuccess()){
                List<HandoverDetailDto> dtoList = handoverBillDto.getDtoList();
                for(HandoverDetailDto handoverDetailDto: dtoList){
                    Performance performance =new Performance();
                    performance.setPerformanceCode(performanceCode);
                    performance.setPoNo(handoverDetailDto.getPoNo());
                    performance.setWaybillCode(handoverDetailDto.getDeliveryOrderId());
                    performance.setGoodName(handoverDetailDto.getSkuName());
                    performance.setGoodNumber(handoverDetailDto.getSkuNum());
                    performance.setPackageCode(handoverDetailDto.getContainerId());
                    list.add(performance);
                }
                pagerResult.setTotal(handoverBillDto.getCountDetail());
                pagerResult.setRows(list);
            }*/
            }else {
                this.logger.warn("通过履约单号查询履约单信息为空"+performanceCode);
            }
        }catch (Exception e){
            this.logger.error("通过履约单号查询履约单信息失败" + performanceCode,e);
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

        HandoverBillPrintReq handoverBillPrint = new HandoverBillPrintReq();
        //HandoverBillPrint handoverBillPrint = new HandoverBillPrint();
        if(StringUtils.isEmpty(performanceCode) || StringUtils.isEmpty(performanceCode.trim())) {
            String waybillCode = waybillorPackCode;
            if (WaybillUtil.isPackageCode(waybillorPackCode)) {
                waybillCode = WaybillUtil.getWaybillCode(waybillorPackCode);
            }
            try {
                performanceCode = waybillCommonService.getPerformanceCode(waybillCode);
                if(performanceCode == null){
                    this.logger.error("通过运单号" + waybillCode + "获得履约单号为空");
                    return JsonHelper.toJson(invokeResult);
                }
            } catch (Exception e) {
                this.logger.error("通过运单号获得运单信息失败" + waybillCode, e);
                return JsonHelper.toJson(invokeResult);
            }
        }

        //GenericResult<List<HandoverDetailDto>> result = null;
        RpcResult<List<HandoverDetailResp>> result = null;
        try {
            handoverBillPrint.setFulfillmentOrderId(performanceCode);

            //result = handoverBillResource.dismantlePrint(handoverBillPrint);
            result = handoverBillProvider.dismantlePrint(handoverBillPrint);
        }catch (Exception e){
            this.logger.error("通过履约单号" + performanceCode + "获得加履单详情失败",e);
            return JsonHelper.toJson(invokeResult);
        }
        if(result != null && result.getValue() != null && result.getValue().size() > 0){

            //-----------------封装数据start-----------------
            //key:箱号，value:该箱号下所有的商品
            Map<String,List<Commodity>> boxMap = new LinkedHashMap<String, List<Commodity>>();
            //key:运单号，value:该运单号下所有箱号
            Map<String,Map<String,List<Commodity>>> waybillMap = new LinkedHashMap<String,Map<String,List<Commodity>>>();
            //key:履约单号，value:该履约单号下所有运单号
            Map<String,Map<String,Map<String,List<Commodity>>>> performanceMap = new LinkedHashMap<String,Map<String,Map<String,List<Commodity>>>>();
            for(HandoverDetailResp resp : result.getValue()){
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

        //HandoverBillPrint handoverBillPrint = new HandoverBillPrint();
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
                    this.logger.error("通过运单号" + waybillCode + "获得履约单号失败");
                    return 3;
                }
                handoverBillPrintReq.setFulfillmentOrderId(performanceCode);
            }catch (Exception e){
                this.logger.error("通过运单号获得履约单号失败" + waybillCode,e);
            }

        }else {
            handoverBillPrintReq.setFulfillmentOrderId(performanceCondition.getPerformanceCode());
        }
        handoverBillPrintReq.setPageNo(1);
        handoverBillPrintReq.setPageSize(10);
        try {
            RpcResult<HandoverBillResp> rpcResult = handoverBillProvider.searchDetail(handoverBillPrintReq);
            if(rpcResult != null && rpcResult.getSuccess()) {
                if(rpcResult.getValue().getCanPrint()){
                    return 1;
                }
                return 2;
            }
        }catch (Exception e){

        }
        return 3;
    }

}
