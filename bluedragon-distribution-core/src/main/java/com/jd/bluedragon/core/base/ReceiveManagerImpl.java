package com.jd.bluedragon.core.base;

import com.jd.bluedragon.common.domain.RepeatPrint;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import com.jd.etms.receive.api.saf.GrossReturnSaf;

@Service("receiveManager")
public class ReceiveManagerImpl implements ReceiveManager{

	/**
     * 接货中心换单接口
     */
    @Autowired
    private GrossReturnSaf receiveExchangeService;
	
	@Override
	public GrossReturnResponse queryDeliveryIdByFcode(String fCode) throws Exception{
		return receiveExchangeService.queryDeliveryIdByFcode(fCode);
	}

	@Override
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
            targetResult.error(ex);
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
            targetResult.error(ex);
        }
        return targetResult;
    }
}
