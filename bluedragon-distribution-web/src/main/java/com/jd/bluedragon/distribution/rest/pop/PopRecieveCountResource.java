package com.jd.bluedragon.distribution.rest.pop;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.popReveice.domain.PopReceive;
import com.jd.bluedragon.distribution.popReveice.service.TaskPopRecieveCountService;
import com.jd.ql.basic.domain.BaseOrg;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class PopRecieveCountResource {
	@Autowired
	private TaskPopRecieveCountService taskPopRecieveCountService;
	
	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@GET
	@POST
	@Path("/test/insertInspection")
	public String insert(){
		Inspection inspection = new Inspection();
		inspection.setQuantity(1);
		inspection.setWaybillCode("170171645");
		inspection.setCreateTime(new Date());
		int n =taskPopRecieveCountService.insert(inspection);	
		return n+"";
	}
	@GET
	@POST
	@Path("/test/insertPopReceive")
	public String insert1(){
		PopReceive popReceive = new PopReceive();
		popReceive.setWaybillCode("777777");
		popReceive.setThirdWaybillCode("11111111");
		popReceive.setOriginalNum(10);
		popReceive.setActualNum(5);
		popReceive.setOperateTime(new Date());
		
		int n =taskPopRecieveCountService.insert(popReceive);	
		return n+"";
	}

	@GET
	@Path("/test/getorgname/{orgId}")
	public String getOrg(@QueryParam("orgId") Integer orgId){
		BaseOrg bo = baseMajorManager.getBaseOrgByOrgId(orgId);
		if(bo!=null)
		return bo.orgName;
		else return "no data";
	}
}
