package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.core.base.ReceiveManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillArgs;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillResult;
import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by wangtingwei on 2014/9/4.
 */
@Service("FWaybillExchangeService")
public class FWaybillExchangeServiceImpl implements FWaybillExchangeService {

    private final Logger log = LoggerFactory.getLogger(FWaybillExchangeServiceImpl.class);

    /**
     * 接货中心换单接口
     */
    @Autowired
    private ReceiveManager receiveManager;

    /**
     * F返单换单
     * @param fWaybillArgs
     * @return
     */
    @Override
    public InvokeResult<FWaybillResult> exchange(FWaybillArgs fWaybillArgs){
        InvokeResult<FWaybillResult> result=new InvokeResult<FWaybillResult>();
        result.setData(new FWaybillResult());
        GrossReturnRequest request=new GrossReturnRequest();
        request.setCustomerId(fWaybillArgs.getBusinessId());
        request.setfCodes( java.util.Arrays.asList(fWaybillArgs.getFWaybills()));
        request.setSiteId(fWaybillArgs.getSiteId());
        request.setSiteName(fWaybillArgs.getSiteName());
        request.setEntryId(fWaybillArgs.getUserId());
        request.setEntryName(fWaybillArgs.getEntryName());
        try {
            GrossReturnResponse response = receiveManager.generateGrossReturnWaybill(request);
            if(200!=response.getResultCode()) {
                result.setCode(response.getResultCode());
                result.setMessage(response.getResultMsg());
			} else {
                result.getData().setDeliveryId(response.getDeliveryId());
                result.getData().setfCodes(response.getfCodes());
                result.getData().setReceiveAddress(response.getReceiveAddress());
                result.getData().setReceiveMobile(response.getReceiveMobile());
                result.getData().setReceiveName(response.getReceiveName());
            }
        }catch (Exception ex){
            this.log.error("返单换单",ex);
            result.setCode(522);
            result.setMessage("调用其它系统接口出现异常");
            result.setData(null);
        }
        return result;
    }
}
