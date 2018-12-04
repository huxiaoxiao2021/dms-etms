package com.jd.bluedragon.distribution.rest.waybill;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.PreseparateWaybillManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.SerialRuleUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by wangtingwei on 2015/10/28.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PreseparateWaybillResource {
    private static final Log logger= LogFactory.getLog(PreseparateWaybillResource.class);

    @Autowired
    private PreseparateWaybillManager preseparateWaybillManager;

    @GET
    @Path("/preseparateWaybill/getPreseparateSiteId/{waybillCode}")
    public com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Integer> getPreseparateSiteId(@PathParam("waybillCode") String waybillCode){
        com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Integer> result=new com.jd.bluedragon.distribution.jsf.domain.InvokeResult<Integer>();
        if(!WaybillUtil.isWaybillCode(waybillCode)){
            if(logger.isInfoEnabled()){
                logger.info("获取预分拣站点，运单号为"+waybillCode);
            }
            result.parameterError("运单号不符合规则");
            return result;
        }
        try{
            result.setData(preseparateWaybillManager.getPreseparateSiteId(waybillCode));
        }catch (Exception ex){
            if(logger.isErrorEnabled()){
                logger.error("获取预分拣站点"+waybillCode,ex);
            }
            result.error(ex);
        }
        return result;
    }
}
