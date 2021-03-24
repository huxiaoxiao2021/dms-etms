package com.jd.bluedragon.distribution.rest.wastePackageStorage;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.request.WastePackageRequest;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.wastePackage.service.WastePackageService;
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
 * 弃件
 * Created by biyubo on 2021/03/22.
 */
@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WastePackageResource {
    private final Logger log = LoggerFactory.getLogger(WastePackageResource.class);

    @Autowired
    private WastePackageService wastePackageService;



    /**
     * 弃件暂存
     * @param
     * @return
     */
    @Path("/waste/wastepackagestorage")
    @POST
    public InvokeResult<Boolean> wastepackagestorage(WastePackageRequest request){
        InvokeResult<Boolean> result = new InvokeResult<>();
        result=wastePackageService.wastepackagestorage(request);

        return result;
    }


}
