package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.systemLog.domain.SystemLog;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SystemLogUtil;
import com.jd.eclp.core.ApiResponse;
import com.jd.eclp.master.export.api.service.dept.DeptServiceApi;
import com.jd.eclp.master.export.api.service.dept.domain.DeptDomain;
import com.jd.eclp.spare.ext.api.common.StringApiResponse;
import com.jd.eclp.spare.ext.api.inbound.InboundCancelRequest;
import com.jd.eclp.spare.ext.api.inbound.InboundOrderService;
import com.jd.eclp.spare.ext.api.inbound.OrderResponse;
import com.jd.eclp.spare.ext.api.inbound.domain.InboundOrder;
import com.jd.kom.ext.service.OrderExtendService;
import com.jd.kom.ext.service.domain.request.SoNoItemRequest;
import com.jd.kom.ext.service.domain.response.ItemInfo;
import com.jd.kom.ext.service.domain.response.KomResponse;
import com.jd.kom.ext.service.domain.response.SoNoItemResponse;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年05月11日 18时:35分
 */
@Service("eclpItemManager")
public class EclpItemManagerImpl implements EclpItemManager {
    private static final Logger log = LoggerFactory.getLogger(EclpItemManagerImpl.class);
    private static Long SYSTEM_LOG_TYPE = 12004L;

    @Autowired
    OrderExtendService orderExtendService;

    @Autowired
    private DeptServiceApi deptServiceApi;

    @Autowired
    private InboundOrderService inboundOrderService;

    @Override
    @JProfiler(jKey = "DMS.BASE.EclpItemManagerImpl.getltemBySoNo", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public List<ItemInfo> getltemBySoNo(String soNo) {
        SoNoItemRequest soNoItemRequest = new SoNoItemRequest();
        soNoItemRequest.setSoNo(soNo);
        soNoItemRequest.setTid(new Date().getTime());
        KomResponse<SoNoItemResponse> komResponse;
        try {
            komResponse = orderExtendService.getItemBySoNo(soNoItemRequest);
        } catch (Exception e) {
            log.error("EclpItemManagerImpl-getltemBySoNo 调用失败:{}",soNo, e);
            return null;
        }
        if (komResponse == null || komResponse.getResultCode() < 1 || komResponse.getData() == null) {
            log.warn("EclpItemManagerImpl-getltemBySoNo 查询失败，查询结果：{},参数：{}" ,JsonHelper.toJson(komResponse), JsonHelper.toJson(soNoItemRequest));
            return null;
        }
        return komResponse.getData().getItemInfoList();
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.EclpItemManagerImpl.getDeptBySettlementOuId", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public String getDeptBySettlementOuId(String ouId) {

        ApiResponse<DeptDomain> apiResponse;
        try{
            apiResponse = deptServiceApi.getDeptBySettlementOuId(null, ouId);
            if(apiResponse != null && apiResponse.getCode() == 1 && apiResponse.getData() != null){
                return apiResponse.getData().getDeptNo();
            }else{
                log.warn("通过结算主体获取目的事业部失败!:{}",ouId);
            }
        }catch (Exception e){
            log.error("EclpItemManagerImpl-getDeptBySettlementOuId 调用失败:{}",ouId, e);
        }
        return null;
    }


    @Override
    @JProfiler(jKey = "DMS.BASE.EclpItemManagerImpl.createInboundOrder", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public OrderResponse createInboundOrder(InboundOrder inboundOrder) {

        try{
            OrderResponse response = inboundOrderService.createInboundOrder(inboundOrder);
            return response;
        }catch (Exception e){
            log.error("EclpItemManagerImpl-createInboundOrder 调用失败：{}",JsonHelper.toJson(inboundOrder), e);
        }
        return null;
    }

    /**
     * 取消入库单
     * @param deptNo
     * @param isvInboundOrderNo
     * @param inboundSource
     * @return
     */
    @Override
    @JProfiler(jKey = "DMS.BASE.EclpItemManagerImpl.cancelInboundOrder", mState = {JProEnum.TP, JProEnum.FunctionError},jAppName= Constants.UMP_APP_NAME_DMSWORKER)
    public boolean cancelInboundOrder(String waybillCode,String deptNo,String isvInboundOrderNo,Byte inboundSource) {

        boolean result = false;
        InboundCancelRequest request = new InboundCancelRequest();
        StringApiResponse response = new StringApiResponse();
        try{

            request.setDeptNo(deptNo);
            request.setIsvInboundOrderNo(isvInboundOrderNo);
            request.setInboundSource(inboundSource);
            response = inboundOrderService.cancelInboundOrder(request);

            result = STRINGAPIRESPONE_SUCCESS == response.getCode();

            if(!result){
                log.warn("EclpItemManagerImpl-cancelInboundOrder返回失败{},{},{},{}",deptNo,isvInboundOrderNo,inboundSource,JsonHelper.toJson(response));
            }
        }catch (Exception e){
            log.error("EclpItemManagerImpl-createInboundOrder 调用异常：{},{},{}",deptNo,isvInboundOrderNo,inboundSource, e);
            response.setCode(Constants.RESULT_ERROR);
            response.setMsg(e.getMessage());
        }finally {
            //记录日志
            pushSystemLog(waybillCode,isvInboundOrderNo,request,response);
        }
        return result;
    }

    /**
     * 记录日志

     */
    private void pushSystemLog(String waybillCode,String isvInboundOrderNo,InboundCancelRequest request, StringApiResponse response){
        try{
            //增加系统日志
            SystemLog sLogDetail = new SystemLog();
            sLogDetail.setKeyword1(waybillCode);
            sLogDetail.setKeyword2(isvInboundOrderNo);
            sLogDetail.setKeyword3("ECLPSpwmsCancel");
            if(response == null){
                sLogDetail.setKeyword4(Long.valueOf(Constants.RESULT_ERROR));
            }else{
                sLogDetail.setKeyword4(Long.valueOf(response.getCode()));
            }
            sLogDetail.setType(SYSTEM_LOG_TYPE);
            sLogDetail.setContent("请求:"+JsonHelper.toJson(request)+"返回:"+JsonHelper.toJson(response));
            SystemLogUtil.log(sLogDetail);
        }catch (Exception e){
            log.error("cancelInboundOrder.pushSystemLog",e);
        }
    }
}
