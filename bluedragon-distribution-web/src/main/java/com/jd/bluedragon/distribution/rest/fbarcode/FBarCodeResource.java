package com.jd.bluedragon.distribution.rest.fbarcode;

import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

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

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class FBarCodeResource {

    private final Log logger = LogFactory.getLog(this.getClass());

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
        this.logger.info("FBarCodeRequest's " + request.toString());

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
        this.logger.info("FBarCodeCode's " + code);
        try {

            result.setData(this.receiveManager.queryDeliveryIdByFcode(code));
            if(null!=result.getData()){
                result.setCode(result.getData().getResultCode());
                result.setMessage(result.getData().getResultMsg());
            }

        } catch (Exception e) {
            logger.error(e);
            result.setCode(JdResponse.CODE_INTERNAL_ERROR);
            result.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
        }
        return result;
    }


}
