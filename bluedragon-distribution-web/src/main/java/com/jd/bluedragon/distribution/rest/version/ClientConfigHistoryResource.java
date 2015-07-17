package com.jd.bluedragon.distribution.rest.version;

import java.util.List;

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
import static com.jd.bluedragon.distribution.api.JdResponse.*;

import com.jd.bluedragon.distribution.rest.version.resp.ClientConfigHistoryResponse;
import com.jd.bluedragon.distribution.version.domain.ClientConfigHistory;
import com.jd.bluedragon.distribution.version.service.ClientConfigHistoryService;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ClientConfigHistoryResource {

	private final Log logger = LogFactory.getLog(this.getClass());

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
		this.logger.info("get all history config "); 
		
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
		
		this.logger.info("siteCode " + siteCode);
		
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

		this.logger.info("programType " + programType);
		
		List<ClientConfigHistory> list =clientConfigHistoryService.getByProgramType(programType);
		if (null !=list) {
			ClientConfigHistoryResponse response=new ClientConfigHistoryResponse(CODE_OK,MESSAGE_OK);
			response.setDatas(list);
			return response;
		}
		return new ClientConfigHistoryResponse(CODE_SERVICE_ERROR,MESSAGE_SERVICE_ERROR);
	}
 
	 
}
