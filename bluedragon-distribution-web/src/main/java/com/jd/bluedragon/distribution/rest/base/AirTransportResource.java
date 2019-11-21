package com.jd.bluedragon.distribution.rest.base;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.AirTransportService;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class AirTransportResource {
	
	/** 日志 */
	private Logger log = LoggerFactory.getLogger(AirTransportResource.class);
	
	@Autowired 
	AirTransportService tAirTransportService;
	
	@GET
	@Path("/bases/air")
	public int getDriver(@QueryParam("supId") Integer supId,@QueryParam("siteCode") Integer siteCode,@QueryParam("receiveCode") Integer receiveCode) {

		try {
			return tAirTransportService.getAirConfig(supId,siteCode,receiveCode);
		} catch (Exception e) {
			log.error("获取航空标示异常:supId={},siteCode={},receiveCode={}",supId,siteCode,receiveCode,e);
			e.printStackTrace();
			return 3;
		}
	}

}
