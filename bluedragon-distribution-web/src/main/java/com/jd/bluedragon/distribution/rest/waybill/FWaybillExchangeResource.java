package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.client.JsonUtil;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillArgs;
import com.jd.bluedragon.distribution.waybill.domain.FWaybillResult;
import com.jd.bluedragon.distribution.waybill.service.FWaybillExchangeService;
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
    public InvokeResult<FWaybillResult> exchange(FWaybillArgs fWaybillArgs){
        //String[] arr=new String[]{"123","dfsa"};
        //fWaybillArgs.setFWaybills(arr);
        if(log.isInfoEnabled()){
            log.info("F返单运单换单调用:{}", JsonUtil.getInstance().object2Json(fWaybillArgs));
            log.info("F返单运单换单调用:{}", fWaybillArgs.toString());
        }
        return exchangeService.exchange(fWaybillArgs);
    }

    /*
    public static void main(String args[]){
        //{"businessId":1,"userId":1,"siteId":1,"siteName":"a","fwaybills":["123","dfsa"]}
        String str="{\"businessId\":1,\"userId\":1,\"siteId\":1,\"siteName\":\"a\",\"fwaybills\":[\"123\",\"dfsa\"]}";
        FWaybillArgs fWaybillArgs = new FWaybillArgs();
        String[] arr=new String[]{"123","dfsa"};
        fWaybillArgs.setFWaybills(arr);
        fWaybillArgs.setSiteName("a");
        fWaybillArgs.setSiteId(1);
        fWaybillArgs.setUserId(1L);
        fWaybillArgs.setBusinessId(1);
        System.out.print(JsonUtil.getInstance().object2Json(fWaybillArgs));
        FWaybillArgs argss=(FWaybillArgs) JsonUtil.getInstance().json2Object(str,FWaybillArgs.class);
        System.out.print(JsonUtil.getInstance().object2Json(fWaybillArgs));
    }
*/

}
