package com.jd.bluedragon.core.base;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import com.jd.etms.receive.api.saf.GrossReturnSaf;

@Service("receiveManager")
public class ReceiveManagerImpl implements ReceiveManager{
	private static final Logger log = LoggerFactory.getLogger(ReceiveManagerImpl.class);
	/**
     * 接货中心换单接口
     */
    @Autowired
    private GrossReturnSaf receiveExchangeService;
	
	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.grossReturnSaf.queryDeliveryIdByFcode",mState={JProEnum.TP,JProEnum.FunctionError})
    public GrossReturnResponse queryDeliveryIdByFcode(String fCode) throws Exception{
		return receiveExchangeService.queryDeliveryIdByFcode(fCode);
	}

	@Override
    @JProfiler(jAppName = Constants.UMP_APP_NAME_DMSWEB,jKey = "dmsWeb.jsf.grossReturnSaf.generateGrossReturnWaybill",mState={JProEnum.TP,JProEnum.FunctionError})
	public GrossReturnResponse generateGrossReturnWaybill(GrossReturnRequest grossReturnRequest) throws Exception{
		return receiveExchangeService.generateGrossReturnWaybill(grossReturnRequest);
	}

    /**
     * 获取新单号【逆向换单】
     * @param oldWaybillCode 旧单号
     * @return
     */
    @Override
    public InvokeResult<String> queryDeliveryIdByOldDeliveryId(String oldWaybillCode){
        CallerInfo callerInfo = Profiler.registerInfo("dmsWeb.jsf.grossReturnSaf.queryDeliveryIdByOldDeliveryId",Constants.UMP_APP_NAME_DMSWEB,false,true);
        InvokeResult<String> targetResult=new InvokeResult<String>();
        try {
            GrossReturnResponse result = receiveExchangeService.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
            if (null != result) {
                targetResult.setCode(result.getResultCode());
                targetResult.setMessage(result.getResultMsg());
                targetResult.setData(result.getDeliveryId());
            } else {
                targetResult.customMessage(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            }
        }catch (Exception ex){
            Profiler.functionError(callerInfo);
            targetResult.error(ex);
        }finally {
            Profiler.registerInfoEnd(callerInfo);
        }
        return targetResult;
    }

    /**
     * 获取新单号【逆向换单】(新)
     * @param oldWaybillCode 旧单号
     * @return
     */
    @Override
    public InvokeResult<RepeatPrint> queryDeliveryIdByOldDeliveryId1(String oldWaybillCode){
        InvokeResult<RepeatPrint> targetResult=new InvokeResult<RepeatPrint>();
        RepeatPrint repeatPrint =new RepeatPrint();
        try {
            GrossReturnResponse result = receiveExchangeService.queryDeliveryIdByOldDeliveryId(oldWaybillCode);
            if (null != result) {
                targetResult.setCode(result.getResultCode());
                targetResult.setMessage(result.getResultMsg());
                repeatPrint.setNewWaybillCode(result.getDeliveryId());
                repeatPrint.setOverTime(false);
                targetResult.setData(repeatPrint);
            } else {
                targetResult.customMessage(InvokeResult.RESULT_NULL_CODE, InvokeResult.RESULT_NULL_MESSAGE);
            }
        }catch (Exception ex){
        	log.error("获取新单号异常！queryDeliveryIdByOldDeliveryId1 入参：{}",oldWaybillCode, ex);
            targetResult.error(ex);
        }
        return targetResult;
    }
}
