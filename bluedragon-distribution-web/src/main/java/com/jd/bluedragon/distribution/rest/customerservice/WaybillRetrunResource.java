package com.jd.bluedragon.distribution.rest.customerservice;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.CustomerServiceResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WaybillRetrunResource {


    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private WaybillQueryManager waybillQueryManager;

	@GET
	@Path("/waybillreturn/{oldBillCode}")
	public CustomerServiceResponse getSurfaceCode(@PathParam("oldBillCode") String oldBillCode) {
		CustomerServiceResponse response = new CustomerServiceResponse();
		try {
			Assert.notNull(oldBillCode, "oldBillCode must not be null");
			this.log.info("oldBillCode's {}", oldBillCode);

			String surFaceCode = this.waybillQueryManager.getChangeWaybillCode(oldBillCode);
			Assert.notNull(surFaceCode, "未检索到新运单号");
			response.setSurfaceCode(surFaceCode);
		} catch (Exception ex) {
			response.setCode(CustomerServiceResponse.CODE_NEW_BILL_CODE_NOT_FOUND);
			response.setMessage(ex.getMessage());
			log.error("检索新运单号异常:{}", oldBillCode, ex);;
		}

		return response;
	}
}
