package com.jd.bluedragon.distribution.rest.mergeWaybillCodeReturn;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.fastjson.JSON;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

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

//    @Autowired
//    private WaybillReturnSignatureApi waybillReturnSignatureApi;
    @GET
    @Path("/packageMake/packageRePrint1/{waybillCode}/{secondWaybillCode}")
    public InvokeResult<String> getNewReturnWaybillCode(@PathParam("waybillCode") String waybillCode,
                                                        @PathParam("secondWaybillCode") String secondWaybillCode){
//        if(waybillCode==null){
//            waybillReturnSignatureApi.queryReturnSignatureMessage(waybillCode);
//        }

        List<String> list = new ArrayList<String>();
        list.add("VA00016114405");
        list.add("VA00016114406");
        list.add("VA00016114407");
        String s = JSON.toJSONString(list);
        return null;
    }
}

