package com.jd.bluedragon.distribution.rest.center;

import java.util.Date;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.resteasy.annotations.GZIP;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.VrsRouteTransferRelationManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.print.domain.PrintWaybill;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

@Component
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class CenterServiceResource {

	private final Log logger = LogFactory.getLog(this.getClass());

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
	
	@Autowired
	private BaseService baseService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@GET
	@Path("/centerService/getBaseSiteBySiteId/")
	@GZIP
	public BaseStaffSiteOrgDto getBaseSiteBySiteId(
			@QueryParam("siteId") Integer siteId) {
		BaseStaffSiteOrgDto result = null;
		try {
			result = baseMajorManager.getBaseSiteBySiteId(siteId);
		} catch (Exception e) {
			logger.error("中心服务调用基础资料getBaseSiteBySiteId出错 siteId=" + siteId, e);
		}
		return result;
	}

	@GET
	@Path("/centerService/getDmsBaseSiteByCode/")
	@GZIP
	public BaseStaffSiteOrgDto getDmsBaseSiteByCode(
			@QueryParam("siteCode") String siteCode) {
		BaseStaffSiteOrgDto result = null;
		try {
			result = baseService.queryDmsBaseSiteByCode(siteCode);
		} catch (Exception e) {
			logger.error("中心服务调用基础资料getDmsBaseSiteByCode出错 siteId=" + siteCode, e);
		}
		return result;
	}

	@GET
	@Path("/centerService/getDataByChoice/")
	@GZIP
	public BaseEntity<BigWaybillDto> getDataByChoice(
			@QueryParam("waybillCode") String waybillCode,
			@QueryParam("isWaybillC") Boolean isWaybillC,
			@QueryParam("isWaybillE") Boolean isWaybillE,
			@QueryParam("isWaybillM") Boolean isWaybillM,
			@QueryParam("isGoodList") Boolean isGoodList,
			@QueryParam("isPackList") Boolean isPackList,
			@QueryParam("isPickupTask") Boolean isPickupTask,
			@QueryParam("isServiceBillPay") Boolean isServiceBillPay) {
		// 判断参数有效性
		if (StringHelper.isEmpty(waybillCode))
			return null;
		if (isWaybillC == null)
			isWaybillC = false;
		if (isWaybillE == null)
			isWaybillE = false;
		if (isWaybillM == null)
			isWaybillM = false;
		if (isGoodList == null)
			isGoodList = false;
		if (isPackList == null)
			isPackList = false;
		if (isPickupTask == null)
			isPickupTask = false;
		if (isServiceBillPay == null)
			isServiceBillPay = false;

		BaseEntity<BigWaybillDto> result = null;
		try {
			result = waybillQueryManager.getDataByChoice(waybillCode,
					isWaybillC, isWaybillE, isWaybillM, isGoodList, isPackList, isPickupTask, isServiceBillPay);
		} catch (Exception e) {
			StringBuilder errorMsg = new StringBuilder(
					"中心服务调用运单getDataByChoice出错").append("waybillCode=")
					.append(waybillCode).append("isWaybillC")
					.append(isWaybillC).append("isWaybillE").append(isWaybillE)
					.append("isWaybillM").append(isWaybillM)
					.append("isPackList").append(isPackList);
			logger.error(errorMsg, e);
		}
		return result;
	}

	@GET
	@Path("/centerService/test/{configType}/{bizzType}/{startsiteCode}/{tositeCode}")
	public PrintWaybill getDmsBaseSiteByCode(@PathParam("configType") Integer configType,
			@PathParam("bizzType") Integer bizzType, @PathParam("startsiteCode") String startsiteCode,
			@PathParam("tositeCode") String tositeCode) {
		PrintWaybill waybill = new PrintWaybill();
		waybill.setPromiseText(vrsRouteTransferRelationManager.queryRoutePredictDate(configType, bizzType,
				startsiteCode, tositeCode, new Date()));
		return waybill;
	}
}
