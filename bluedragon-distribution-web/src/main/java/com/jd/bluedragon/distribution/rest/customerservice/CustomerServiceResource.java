package com.jd.bluedragon.distribution.rest.customerservice;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.response.CustomerServiceResponse;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.PickupTask;
import com.jd.etms.waybill.wss.PickupTaskWS;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

//import com.jd.etms.erp.ws.ErpQuerySafWS;
//import com.jd.etms.waybill.wss.ErpQuerySafWS

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class CustomerServiceResource {


    private final Log logger = LogFactory.getLog(this.getClass());

    private static final String oldPrefix = "Q";

    @Autowired
    private PickupTaskWS pickupWebService;


    @GET
    @Path("/customerservice/{oldBillCode}")
    public CustomerServiceResponse getSurfaceCode(@PathParam("oldBillCode") String oldBillCode) {
        CustomerServiceResponse response= new CustomerServiceResponse();
        try {
            Assert.notNull(oldBillCode, "oldBillCode must not be null");
            Assert.isTrue(oldBillCode.startsWith(oldPrefix), "请输入正确的取件单号!");
            this.logger.info("oldBillCode's " + oldBillCode);

            BaseEntity<PickupTask> task = this.pickupWebService.getPickTaskByPickCode(oldBillCode);
            String surFaceCode="";
            PickupTask pickupTask = task.getData();
            surFaceCode = pickupTask.getSurfaceCode();
            response.setSurfaceCode(surFaceCode);
            response.setServiceCode(pickupTask.getServiceCode());
            logger.info("外单逆向换单旧单号"+oldBillCode+JsonHelper.toJson(response));
        } catch (Exception ex) {
            response.setCode(CustomerServiceResponse.CODE_NEW_BILL_CODE_NOT_FOUND);
            response.setMessage(ex.getMessage());
            logger.error("外单逆向换单获取新运单号失败,旧单号："+oldBillCode+ex);
        }

        return response;
    }
}
