package com.jd.bluedragon.distribution.rest.customerservice;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.CustomerServiceResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.perf4j.aop.Profiled;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import com.jd.etms.erp.ws.ErpQuerySafWS;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WaybillRetrunResource {


    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private ErpQuerySafWS baseErpQuerySafWSInfoSafService;


    @GET
    @Path("/waybillreturn/{oldBillCode}")
    @Profiled(tag = "CustomerServiceResource.get")
    public CustomerServiceResponse getSurfaceCode(@PathParam("oldBillCode") String oldBillCode) {
        CustomerServiceResponse response= new CustomerServiceResponse();
        try {
            Assert.notNull(oldBillCode, "oldBillCode must not be null");
            this.logger.info("oldBillCode's " + oldBillCode);

            String surFaceCode="";
            surFaceCode = this.baseErpQuerySafWSInfoSafService.getChangeWaybillCode(oldBillCode).getData();
            Assert.notNull(surFaceCode, "未检索到新运单号");
            response.setSurfaceCode(surFaceCode);
        } catch (Exception ex) {
            response.setCode(CustomerServiceResponse.CODE_NEW_BILL_CODE_NOT_FOUND);
            response.setMessage(ex.getMessage());
        }

        return response;
    }
}
