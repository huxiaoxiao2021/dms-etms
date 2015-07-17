package com.jd.bluedragon.distribution.waybill.service;

import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillArgs;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillResult;
import com.jd.etms.receive.api.request.GrossReturnRequest;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import com.jd.etms.receive.api.saf.GrossReturnSaf;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Created by wangtingwei on 2014/9/4.
 */
@Service("FWaybillExchangeService")
public class FWaybillExchangeServiceImpl implements FWaybillExchangeService {

    private final Log logger= LogFactory.getLog(FWaybillExchangeServiceImpl.class);

    /**
     * 接货中心换单接口
     */
    @Autowired
    private GrossReturnSaf receiveExchangeService;

    /**
     * F返单换单
     * @param fWaybillArgs
     * @return
     */
    @Override
    public InvokeResult<FWaybillResult> exchange(FWaybillArgs fWaybillArgs){
        InvokeResult<FWaybillResult> result=new InvokeResult<FWaybillResult>();
        result.setCode(200);
        result.setMessage("OK");
        result.setData(new FWaybillResult());
        GrossReturnRequest request=new GrossReturnRequest();
        request.setCustomerId(fWaybillArgs.getBusinessId());
        request.setfCodes( java.util.Arrays.asList(fWaybillArgs.getFWaybills()));
        request.setSiteId(fWaybillArgs.getSiteId());
        request.setSiteName(fWaybillArgs.getSiteName());
        request.setEntryId(fWaybillArgs.getUserId());
        request.setEntryName(fWaybillArgs.getEntryName());
        try {
            GrossReturnResponse response = receiveExchangeService.generateGrossReturnWaybill(request);
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
            this.logger.error(ex);
            result.setCode(522);
            result.setMessage("调用其它系统接口出现异常");
            result.setData(null);
        }
        return result;
    }
}
