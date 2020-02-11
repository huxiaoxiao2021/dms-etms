package com.jd.bluedragon.distribution.rest.fbarcode;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.ReceiveManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.FBarCodeRequest;
import com.jd.bluedragon.distribution.api.response.FBarCodeResponse;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.fBarCode.domain.FBarCode;
import com.jd.bluedragon.distribution.fBarCode.service.FBarCodeService;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.receive.api.response.GrossReturnResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class FBarCodeResource {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private FBarCodeService fBarCodeService;

    @Autowired
    ReceiveManager receiveManager;


    @POST
    @Path("/fbarcodes")
    public FBarCodeResponse getBatchFBarCodes(FBarCodeRequest request) {
        Assert.notNull(request, "request must not be null");
        Assert.notNull(request.getCreateSiteCode(), "request createSiteCode must not be null");
        Assert.isTrue(request.getQuantity() != 0, "request quantity must not be zero");
        this.log.info("FBarCodeRequest's {}", request.toString());

        FBarCodeResponse fbc = new FBarCodeResponse();
        StringBuilder sb = new StringBuilder();
        List<String> lst = Lists.newArrayList();

        List<FBarCode> lstFBarCode= fBarCodeService.batchAdd(this.toFbarCode(request));
        for (int i = 0; i < lstFBarCode.size(); i++) {
            lst.add(lstFBarCode.get(i).getCode());
        }

        fbc.setfBarCodeCodes(StringHelper.join(lst, ","));
        fbc.setCreateSiteCode(request.getCreateSiteCode());
        fbc.setCreateSiteName(request.getCreateSiteName());
        return fbc;
    }

    private FBarCode toFbarCode(FBarCodeRequest request) {
        FBarCode fBarCode = new FBarCode();
        fBarCode.setQuantity(request.getQuantity());
        fBarCode.setCreateSiteCode(request.getCreateSiteCode());
        fBarCode.setCreateSiteName(request.getCreateSiteName());
        fBarCode.setCreateUser(request.getUserName());
        fBarCode.setCreateUserCode(request.getUserCode());
        return fBarCode;
    }

    @GET
    @Path("fbarcode/getwaybillfbarcodes/{code}")
    public InvokeResult<GrossReturnResponse> getWaybillFBarCodes(@PathParam("code") String code ) {
        InvokeResult<GrossReturnResponse> result=new InvokeResult<GrossReturnResponse>();
        Assert.notNull(code, "fbarcode must not be null");
        this.log.info("FBarCodeCode's {}", code);
        try {

            result.setData(this.receiveManager.queryDeliveryIdByFcode(code));
            if(null!=result.getData()){
                result.setCode(result.getData().getResultCode());
                result.setMessage(result.getData().getResultMsg());
            }

        } catch (Exception e) {
            log.error("getWaybillFBarCodes异常：",e);
            result.setCode(JdResponse.CODE_INTERNAL_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }


}
