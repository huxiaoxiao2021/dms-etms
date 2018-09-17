package com.jd.bluedragon.distribution.rest.mergeWaybillCodeReturn;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.fastjson.JSON;
import com.jd.ldop.center.api.ResponseDTO;
import com.jd.ldop.center.api.reverse.WaybillReturnSignatureApi;
import com.jd.ldop.center.api.reverse.dto.ReturnSignatureMessageDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

/**
 * @ClassName: 123
 * @Description: 123
 * @author: hujiping
 * @date: 2018/9/15 17:07
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class MergeWaybillCodeReturnResource {

    @Autowired
    private WaybillReturnSignatureApi waybillReturnSignatureApi;
    @GET
    @Path("/getNewReturnWaybillCode/{waybillCode}/{secondWaybillCode}")
    public InvokeResult<String> getNewReturnWaybillCode(@PathParam("waybillCode") String waybillCode,
                                                        @PathParam("secondWaybillCode") String secondWaybillCode){
        ResponseDTO<ReturnSignatureMessageDTO> responseDto = null;
        if(waybillCode==null){
            responseDto = waybillReturnSignatureApi.queryReturnSignatureMessage(waybillCode);
        }

        InvokeResult<String> result = new InvokeResult<String>();
        result.setCode(InvokeResult.RESULT_SUCCESS_CODE);
        return result;
    }

    @POST
    @Path("")
    public void mergeWaybill(){

        //给运单发消息

        //发全程跟踪

    }
}

