package com.jd.bluedragon.distribution.rest.residentPrint;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillTraceManager;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.print.waybill.handler.WaybillPrintMessages;
import com.jd.bluedragon.distribution.residentPrint.service.ResidentService;
import com.jd.etms.waybill.domain.PackageState;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ResidentResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ResidentService residentService;

    @Autowired
    private WaybillTraceManager waybillTraceManager;

    /**
     * 查询运单号是否操作站点发货
     * @param boxCode
     * @return
     */
    @GET
    @Path("/resident/isSendBySite/{boxCode}")
    public InvokeResult<String> isSendBySite(@PathParam("boxCode") String boxCode){
        InvokeResult<String> result = new InvokeResult<String>();
        String waybillCode = null;
        try{
            List<String> waybillCodes = residentService.getAllWaybillCodeByBoxCode(boxCode);
            if(waybillCodes != null && waybillCodes.size() >0){
                waybillCode = waybillCodes.get(0);
                List<PackageState> list = waybillTraceManager.getPkStateByWCodeAndState(waybillCode, Constants.WAYBILL_TRACE_STATE_SEND_BY_SITE);
                if(list != null && list.size() > 0){
                    result.setData(WaybillPrintMessages.MESSAGE_WAYBILL_STATE_SEND_BY_SITE);
                }
            }
        }catch (Exception e){
            this.logger.error("通过运单号"+waybillCode+"查询运单全程跟踪失败！");
            result.setCode(InvokeResult.SERVER_ERROR_CODE);
            result.setMessage(InvokeResult.SERVER_ERROR_MESSAGE);
        }
        return result;
    }

}
