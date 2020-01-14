package com.jd.bluedragon.distribution.rest.version;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.rest.version.resp.ClientConfigHistoryResponse;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;
import com.jd.bluedragon.distribution.version.service.ClientConfigHistoryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.jd.bluedragon.distribution.api.JdResponse.*;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ClientConfigHistoryResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ClientConfigHistoryService clientConfigHistoryService;

	/**
	 * 查询所有的配置变更历史信息
	 * 
	 * @return
	 */
	@GET
	@Path("/versions/configHistory/getAll")
	public ClientConfigHistoryResponse getAll() {
		this.log.debug("get all history config ");
		
		List<ClientConfigHistory> list =clientConfigHistoryService.getAll();
		if (null !=list) {
			ClientConfigHistoryResponse response=new ClientConfigHistoryResponse(CODE_OK,MESSAGE_OK);
			response.setDatas(list);
			return response;
		}
		return new ClientConfigHistoryResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}

	/**
	 * 依据分拣中心编号查询配置变更历史信息
	 * 
	 * @param siteCode
	 * @return
	 */
	@GET
	@Path("/versions/configHistory/getBySiteCode/{siteCode}")
	public ClientConfigHistoryResponse getBySiteCode(
			@PathParam("siteCode") String siteCode) {
		Assert.notNull(siteCode, "siteCode must not be null");
		
		this.log.debug("siteCode ：{}", siteCode);
		
		List<ClientConfigHistory> list =clientConfigHistoryService.getBySiteCode(siteCode);
		if (null !=list) {
			ClientConfigHistoryResponse response=new ClientConfigHistoryResponse(CODE_OK,MESSAGE_OK);
			response.setDatas(list);
			return response;
		}
		return new ClientConfigHistoryResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}

	/**
	 * 依据应用程序类型查询所有的配置变更历史信息
	 * 
	 * @param programType
	 * @return
	 */
	@GET
	@Path("/versions/configHistory/getByProgramType/{programType}")
	public ClientConfigHistoryResponse getByProgramType(
			@PathParam("programType") Integer programType) {
		Assert.notNull(programType, "programType must not be null");

		this.log.debug("programType :{}", programType);
		
		List<ClientConfigHistory> list =clientConfigHistoryService.getByProgramType(programType);
		if (null !=list) {
			ClientConfigHistoryResponse response=new ClientConfigHistoryResponse(CODE_OK,MESSAGE_OK);
			response.setDatas(list);
			return response;
		}
		return new ClientConfigHistoryResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}
 
	 
}
