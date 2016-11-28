package com.jd.bluedragon.distribution.rest.customerservice;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
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
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.api.response.CustomerServiceResponse;

@Component
@Path(Constants.REST_URL)
@Consumes({MediaType.APPLICATION_JSON})
@Produces({MediaType.APPLICATION_JSON})
public class WaybillRetrunResource {


    private final Log logger = LogFactory.getLog(this.getClass());

    @Autowired
    private WaybillQueryManager waybillQueryManager;

	@GET
	@Path("/waybillreturn/{oldBillCode}")
	public CustomerServiceResponse getSurfaceCode(@PathParam("oldBillCode") String oldBillCode) {
		CustomerServiceResponse response = new CustomerServiceResponse();
		try {
			Assert.notNull(oldBillCode, "oldBillCode must not be null");
			this.logger.info("oldBillCode's " + oldBillCode);

			String surFaceCode = this.waybillQueryManager.getChangeWaybillCode(oldBillCode);
			Assert.notNull(surFaceCode, "未检索到新运单号");
			response.setSurfaceCode(surFaceCode);
		} catch (Exception ex) {
			response.setCode(CustomerServiceResponse.CODE_NEW_BILL_CODE_NOT_FOUND);
			response.setMessage(ex.getMessage());
			logger.error("检索新运单号异常:"+oldBillCode, ex);;
		}

		return response;
	}
}
