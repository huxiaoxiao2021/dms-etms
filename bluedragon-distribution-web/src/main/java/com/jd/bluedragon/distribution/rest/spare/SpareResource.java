package com.jd.bluedragon.distribution.rest.spare;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.SpareRequest;
import com.jd.bluedragon.distribution.api.response.SpareResponse;
import com.jd.bluedragon.distribution.spare.domain.Spare;
import com.jd.bluedragon.distribution.spare.service.SpareService;
import com.jd.bluedragon.utils.StringHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class SpareResource {
    
    private final Log logger = LogFactory.getLog(this.getClass());
    
    @Autowired
    private SpareService spareService;
    
    @GET
    @Path("/spares/{spareCode}")
    public SpareResponse get(@PathParam("spareCode") String spareCode) {
        Assert.notNull(spareCode, "spareCode must not be null");
        this.logger.info("spare code's " + spareCode);
        
        Spare spare = this.spareService.findBySpareCode(spareCode);
        if (spare == null) {
            return this.spareNoFound();
        }
        return this.toSpareResponse(spare);
    }
    
    @POST
    @Path("/spares/reprint")
    public SpareResponse reprint(SpareRequest request) {
        Assert.notNull(request.getSpareCode(), "SpareRequest's code must not be null");
        this.logger.info("SpareRequest's " + request);
        this.spareService.reprint(this.toSpare2(request));
        return this.ok();
    }
    
    @POST
    @Path("/spares")
    public SpareResponse print(SpareRequest request) {
        Assert.notNull(request, "request must not be null");
        this.logger.info("SpareRequest's " + request);
        
        Spare spare = this.toSpare(request);
        List<Spare> availableSpares = this.spareService.print(spare);
        
        SpareResponse response = this.ok();
        response.setSpareCodes(StringHelper.join(availableSpares, "getCode",
                Constants.SEPARATOR_COMMA));
        return response;
    }
    
    private SpareResponse toSpareResponse(Spare spare) {
        SpareResponse response = this.ok();
        response.setSpareCode(spare.getCode());
        response.setType(spare.getType());
        return response;
    }
    
    private Spare toSpare(SpareRequest request) {
        Spare spare = new Spare();
        spare.setType(request.getType());
        spare.setQuantity(request.getQuantity());
        spare.setCreateUser(request.getUserName());
        spare.setCreateUserCode(request.getUserCode());
        spare.setTimes(Spare.DEFAULT_TIMES);
        spare.setStatus(Spare.STATUS_DEFAULT);
        return spare;
    }
    
    private Spare toSpare2(SpareRequest request) {
        Spare spare = new Spare();
        spare.setCode(request.getSpareCode());
        spare.setUpdateUser(request.getUserName());
        spare.setUpdateUserCode(request.getUserCode());
        return spare;
    }
    
    private SpareResponse spareNoFound() {
        return new SpareResponse(SpareResponse.CODE_SPARE_NOT_FOUND,
                SpareResponse.MESSAGE_SPARE_NOT_FOUND);
    }
    
    private SpareResponse ok() {
        return new SpareResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
    }
    
}
