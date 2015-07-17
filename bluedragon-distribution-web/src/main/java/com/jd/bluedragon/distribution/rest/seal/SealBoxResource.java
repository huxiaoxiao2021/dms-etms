package com.jd.bluedragon.distribution.rest.seal;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SealBoxRequest;
import com.jd.bluedragon.distribution.api.response.SealBoxResponse;
import com.jd.bluedragon.distribution.seal.domain.SealBox;
import com.jd.bluedragon.distribution.seal.service.SealBoxService;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SealBoxResource {

    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private SealBoxService sealBoxService;

    @GET
    @Path("/seal/box/{sealCode}")
    public SealBoxResponse findSealByCode(@PathParam("sealCode") String sealCode) {
        Assert.notNull(sealCode, "sealCode must not be null");
        this.logger.info("sealCode is " + sealCode);

        SealBox sealBox = this.sealBoxService.findBySealCode(sealCode);
        if (sealBox == null) {
            return this.sealBoxNoFound();
        }

        return SealBox.toSealBoxResponse(sealBox);
    }
    
    /**
     * cyk
     * @param boxCode 箱号
     * @return SealBox信息
     */
    @GET
    @Path("/seal/box/{boxCode}")
    public SealBoxResponse findSealByBoxCode(@PathParam("boxCode") String boxCode) {
        Assert.notNull(boxCode, "boxCode must not be null");
        this.logger.info("boxCode is " + boxCode);

        SealBox sealBox = this.sealBoxService.findByBoxCode(boxCode);
        if (sealBox == null) {
            return this.sealBoxNoFound();
        }

        return SealBox.toSealBoxResponse(sealBox);
    }

    @POST
    @Path("/seal/box")
    public SealBoxResponse add(SealBoxRequest request) {
        Assert.notNull(request, "request must not be null");
        this.logger.info("SealBoxRequest's " + request);

        this.sealBoxService.saveOrUpdate(SealBox.toSealBox(request));
        return this.ok();
    }

    @PUT
    @Path("/seal/box")
    public SealBoxResponse update(SealBoxRequest request) {
        Assert.notNull(request, "request must not be null");
        this.logger.info("SealBoxRequest's " + request);

        this.sealBoxService.saveOrUpdate(SealBox.toSealBox2(request));
        return this.ok();
    }

    private SealBoxResponse sealBoxNoFound() {
        return new SealBoxResponse(SealBoxResponse.CODE_SEAL_BOX_NOT_FOUND,
                SealBoxResponse.MESSAGE_SEAL_BOX_NOT_FOUND);
    }

    private SealBoxResponse ok() {
        return new SealBoxResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }

}
