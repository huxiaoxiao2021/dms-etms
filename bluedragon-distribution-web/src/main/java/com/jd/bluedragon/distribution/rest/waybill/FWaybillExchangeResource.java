package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.JsonUtil;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillArgs;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillResult;
import com.jd.bluedragon.distribution.waybill.service.FWaybillExchangeService;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * F返单换单
 * Created by wangtingwei on 2014/9/4.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class FWaybillExchangeResource {

    private final Logger log = LoggerFactory.getLogger(FWaybillExchangeResource.class);

    /**
     * F返单换单服务
     */
    @Autowired
    private FWaybillExchangeService exchangeService;
    /**
     * F返单换单
     * @param fWaybillArgs
     * @return
     */
    @Path("/fwaybill/exchange")
    @POST
    @JProfiler(jKey = "DMS.WEB.FWaybillExchangeResource.exchange", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    public InvokeResult<FWaybillResult> exchange(FWaybillArgs fWaybillArgs){
        //String[] arr=new String[]{"123","dfsa"};
        //fWaybillArgs.setFWaybills(arr);
        if(log.isInfoEnabled()){
            log.info("F返单运单换单调用:{}", JsonUtil.getInstance().object2Json(fWaybillArgs));
            log.info("F返单运单换单调用:{}", fWaybillArgs.toString());
        }
        return exchangeService.exchange(fWaybillArgs);
    }
}
