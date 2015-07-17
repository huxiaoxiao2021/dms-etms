package com.jd.bluedragon.distribution.rest.reverse;


import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.reverse.service.ReverseSendPopMessageService;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ReversePopMessageResource {
	@Autowired
	private ReverseSendPopMessageService reverseSendPopMessageService;
	@GET
	@Path("/popMessage/sendMessage/{waybillCode}")
	public String sendMessage(@PathParam("waybillCode") String waybillCode) throws Throwable{
		String result = reverseSendPopMessageService.sendPopMessageForTest(waybillCode);
		return result;
	}

}
