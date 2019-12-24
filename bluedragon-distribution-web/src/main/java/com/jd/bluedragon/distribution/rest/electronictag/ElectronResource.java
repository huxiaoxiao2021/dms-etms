package com.jd.bluedragon.distribution.rest.electronictag;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.ElectronSiteResponse;
import com.jd.bluedragon.distribution.electron.domain.ElectronSite;
import com.jd.bluedragon.distribution.electron.service.ElectronSiteService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
 * 根据订单包裹号 获取电子标签信息
 * 
 * @author guoyongzhi
 * 
 */
@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class ElectronResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ElectronSiteService electronSiteService;


	/**
	 * 根据分拣中心ID / 订单或包裹号 获取任务区信息
	 * 
	 * @param dmsID
	 * @param waybillorPackCode
	 * @return
	 */
	@GET
	@Path("/electronictag/{dmsID}/{waybillorPackCode}")
	public ElectronSiteResponse get(@PathParam("dmsID") Integer dmsID,
			@PathParam("waybillorPackCode") String waybillorPackCode) {
		Assert.notNull(dmsID, "dmsID must not be null");
		Assert.notNull(waybillorPackCode, "waybillorPackCode must not be null");
		this.log.info("dmsID：  {}", dmsID);
		this.log.info("waybillorPackCode：  {}", waybillorPackCode);
		ElectronSite electronSite = new ElectronSite();
		String aWaybillCode = WaybillUtil.getWaybillCode(waybillorPackCode);
		electronSite = electronSiteService.getElecSiteInfo(dmsID, aWaybillCode);
		if (electronSite == null) {
			return this.NotFound();
		} else if (JdResponse.CODE_OK.equals(electronSite.getCode())) {
			return this.OK(electronSite);
		} else {
			return Fail(electronSite);
		}

	}
	
	/**
	 * 根据分拣中心ID / 任务区信息 判断是否存在任务区
	 * 
	 * @param dmsID
	 * @param taskAreaNo
	 * @return
	 */
	@GET
	@Path("/taskAreaNo/{dmsID}/{taskAreaNo}")
	public ElectronSiteResponse getTaskAreaNo(@PathParam("dmsID") Integer dmsID,
			@PathParam("taskAreaNo") Integer taskAreaNo) {
		Assert.notNull(dmsID, "dmsID must not be null");
		Assert.notNull(taskAreaNo, "waybillorPackCode must not be null");
		this.log.info("dmsID：   {}", dmsID);
		this.log.info("taskAreaNo：  {}  ", taskAreaNo);
		ElectronSite electronSite = new ElectronSite();
		electronSite = electronSiteService.getTaskAreaNo(dmsID, taskAreaNo);
		if (electronSite == null) {
			return this.NotFound();
		} else if (JdResponse.CODE_OK.equals(electronSite.getCode())) {
			return this.OK(electronSite);
		} else {
			return Fail(electronSite);
		}

	}

	private ElectronSiteResponse NotFound() {
		return new ElectronSiteResponse(
				ElectronSiteResponse.CODE_Electron_NOT_FOUND,
				ElectronSiteResponse.MESSAGE_Electron_NOT_FOUND);
	}

	private ElectronSiteResponse Fail(ElectronSite electronSite) {
		ElectronSiteResponse electronSiteResponse = new ElectronSiteResponse();
		electronSiteResponse.setCode(electronSite.getCode());
		electronSiteResponse.setMessage(electronSite.getMessage());

		return electronSiteResponse;

	}

	private ElectronSiteResponse OK(ElectronSite electronSite) {
		ElectronSiteResponse electronSiteResponse = new ElectronSiteResponse();
		electronSiteResponse.setCode(JdResponse.CODE_OK);
		electronSiteResponse.setMessage(JdResponse.MESSAGE_OK);
		electronSiteResponse.setElectronNo(electronSite.getElectronNo());
		electronSiteResponse.setTaskAreaNo(electronSite.getTaskAreaNo());
		electronSiteResponse.setIp(electronSite.getIp());
		return electronSiteResponse;

	}
}
