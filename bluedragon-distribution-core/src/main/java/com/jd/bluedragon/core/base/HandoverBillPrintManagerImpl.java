package com.jd.bluedragon.core.base;

import com.jd.b2b.wt.assemble.sdk.RpcResult;
import com.jd.b2b.wt.assemble.sdk.req.HandoverBillPrintReq;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverBillResp;
import com.jd.b2b.wt.assemble.sdk.resp.HandoverDetailResp;
import com.jd.b2b.wt.assemble.sdk.service.HandoverBillProvider;
import com.jd.bluedragon.Constants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author lijie
 * @date 2019/8/22 16:55
 */
@Service("handoverBillPrintManager")
public class HandoverBillPrintManagerImpl implements HandoverBillPrintManager{

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private HandoverBillProvider handoverBillProvider;

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.searchHandoverDetail" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public HandoverBillResp searchHandoverDetail(HandoverBillPrintReq handoverBillPrintReq) {
        RpcResult<HandoverBillResp> rpcResult = handoverBillProvider.searchDetail(handoverBillPrintReq);
        if (rpcResult != null && rpcResult.getSuccess()) {
            return rpcResult.getValue();
        }
        this.logger.warn("通过履约单号查询履约单信息为空"+handoverBillPrintReq.getFulfillmentOrderId());
        return null;
    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.searchHandoverisCanPrint" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public Boolean searchHandoverisCanPrint(HandoverBillPrintReq handoverBillPrintReq) {
        RpcResult<HandoverBillResp> rpcResult = handoverBillProvider.searchDetail(handoverBillPrintReq);
        if(rpcResult != null && rpcResult.getSuccess()){
            return rpcResult.getValue().getCanPrint();
        }
        this.logger.warn("通过履约单号查询能否打印信息为空"+handoverBillPrintReq.getFulfillmentOrderId());
        return false;

    }

    @Override
    @JProfiler(jKey = "DMS.BASE.WaybillQueryManagerImpl.dismantlePrint" , jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
    public List<HandoverDetailResp> dismantlePrint(HandoverBillPrintReq handoverBillPrintReq) {
        RpcResult<List<HandoverDetailResp>> result = null;
        try {
            result = handoverBillProvider.dismantlePrint(handoverBillPrintReq);
        }catch (Exception e){
            this.logger.error("通过履约单号" + handoverBillPrintReq.getFulfillmentOrderId() + "获得加履单详情失败",e);
            return null;
        }
        if(result != null){
            return result.getValue();
        }
        return null;
    }
}
