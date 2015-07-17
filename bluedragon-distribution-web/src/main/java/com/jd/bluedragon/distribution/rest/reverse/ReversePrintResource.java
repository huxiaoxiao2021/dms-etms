package com.jd.bluedragon.distribution.rest.reverse;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ReversePrintRequest;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.reverse.service.ReversePrintService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
 * 外单逆向换单打印
 * Created by wangtingwei on 14-8-7.
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ReversePrintResource {
;
    private static final Log logger= LogFactory.getLog(ReversePrintResource.class);

    @Autowired
    private ReversePrintService reversePrintService;
    /**
     * 外单逆向换单打印提交数据
     * @return JSON【{code: message: data:}】
     */
    @POST
    @Path("/reverse/exchange/print")
    public WaybillResponse<Boolean> handlePrint(ReversePrintRequest request){
        logger.info("【逆向换单处理打印数据】"+request.toString());
        WaybillResponse<Boolean> result=new WaybillResponse<Boolean>();
        try{
            reversePrintService.handlePrint(request);
            result.setCode(JdResponse.CODE_OK);
            result.setMessage(JdResponse.MESSAGE_OK);
            result.setData(Boolean.TRUE);
        }
        catch (Exception e){
            logger.error("【逆向换单】",e);
            result.setCode(JdResponse.CODE_SERVICE_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            result.setData(Boolean.FALSE);
        }
        return result;
    }
}
