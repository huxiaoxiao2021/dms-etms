package com.jd.bluedragon.distribution.rest.quickProduce;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * Created by yanghongqiang on 2015/9/10.
 */

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class QuickProduceResource {


    /**
     * 根据运单号或包裹号获取运单包裹信息接口
     * 新接口调用预分拣接口获取基础资料信息
     *
     * @param waybillCode Or package
     * @return
     */
    @GET
    @Path("/quickProduce/getwaybillPrePack/{startDmsCode}/{waybillCodeOrPackage}/{localSchedule}/{paperless}")
    public WaybillResponse<Waybill> getwaybillPrePack(@PathParam("startDmsCode") Integer startDmsCode,
                                                      @PathParam("waybillCodeOrPackage") String waybillCodeOrPackage, @PathParam("localSchedule") Integer localSchedule
            , @PathParam("paperless") Integer paperless) {

        return  null;
    }

}


