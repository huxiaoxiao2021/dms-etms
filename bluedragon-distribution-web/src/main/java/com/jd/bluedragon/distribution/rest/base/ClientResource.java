package com.jd.bluedragon.distribution.rest.base;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.base.service.UserService;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientHeartbeatResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginRequest;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLoginResponse;
import com.jd.bluedragon.sdk.modules.client.dto.DmsClientLogoutRequest;
import com.jd.bluedragon.service.remote.client.DmsClientManager;


@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class ClientResource {

	private final static Logger log = LoggerFactory.getLogger(ClientResource.class);
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private DmsClientManager dmsClientManager;
	
	@POST
	@Path("/client/login")
	public JdResult<DmsClientLoginResponse> login(DmsClientLoginRequest dmsClientLoginRequest){
		return dmsClientManager.login(dmsClientLoginRequest);
	}
	
	@POST
	@Path("/client/logout")
	public JdResult<Boolean> logout(DmsClientLogoutRequest dmsClientLogoutRequest){
		return dmsClientManager.logout(dmsClientLogoutRequest);
	}
	
	@POST
	@Path("/client/sendHeartbeat")
	public JdResult<DmsClientHeartbeatResponse> sendHeartbeat(DmsClientHeartbeatRequest dmsClientHeartbeatRequest){
		return userService.sendHeartbeat(dmsClientHeartbeatRequest);
	}
}


