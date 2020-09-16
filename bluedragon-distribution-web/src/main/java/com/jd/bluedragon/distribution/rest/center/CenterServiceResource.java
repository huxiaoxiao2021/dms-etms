package com.jd.bluedragon.distribution.rest.center;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.core.base.*;
import com.jd.bluedragon.distribution.jsf.service.JsfSortingResourceService;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.NumberHelper;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.ldop.basic.dto.BasicTraderInfoDTO;
import com.jd.ldop.center.api.print.WaybillPrintApi;
import com.jd.ldop.center.api.print.dto.PrintResultDTO;
import com.jd.ldop.center.api.print.dto.WaybillPrintRequestDTO;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.jd.bluedragon.Constants;
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

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private BaseMajorManager baseMajorManager;
	
	@Autowired
	private VrsRouteTransferRelationManager vrsRouteTransferRelationManager;
	
	@Autowired
	private BaseService baseService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

	@Autowired
	private WaybillPrintApi waybillPrintApi;

	@Autowired
	private BaseMinorManager baseMinorManager;

	@Autowired
	private WaybillPackageManager waybillPackageManager;

	@Autowired
	private JsfSortingResourceService jsfSortingResourceService;

	@GET
	@Path("/centerService/getBaseSiteBySiteId/")
	@GZIP
	public BaseStaffSiteOrgDto getBaseSiteBySiteId(
			@QueryParam("siteId") Integer siteId) {
		BaseStaffSiteOrgDto result = null;
		try {
			result = baseMajorManager.getBaseSiteBySiteId(siteId);
		} catch (Exception e) {
			log.error("中心服务调用基础资料getBaseSiteBySiteId出错 siteId={}", siteId, e);
		}
		return result;
	}

    /**
     * 根据用户的erp或者StaffID查询用户信息
     * @param userCode
     * @return
     */
	@GET
	@Path("/centerService/getUserInfoByCode/{userCode}")
	@GZIP
	public BaseStaffSiteOrgDto getUserInfoByCode(@PathParam("userCode") String userCode) {
		BaseStaffSiteOrgDto result = null;
		try {
		    if(NumberHelper.isNumber(userCode)){
                result = baseMajorManager.getBaseStaffByStaffId(Integer.valueOf(userCode));
            }else{
                result = baseMajorManager.getBaseStaffByErpNoCache(userCode);
            }

		} catch (Exception e) {
			log.error("中心服务调用基础资料getBaseStaffByErpNoCache出错 userCode={}", userCode, e);
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
			log.error("中心服务调用基础资料getDmsBaseSiteByCode出错 siteId={}", siteCode, e);
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
			log.info("获取运单数据waybillCode[{}],data[{}]result[{}]",waybillCode, result.getData() == null, JsonHelper.toJson(result));
		} catch (Exception e) {
			StringBuilder errorMsg = new StringBuilder(
					"中心服务调用运单getDataByChoice出错").append("waybillCode=")
					.append(waybillCode).append("isWaybillC")
					.append(isWaybillC).append("isWaybillE").append(isWaybillE)
					.append("isWaybillM").append(isWaybillM)
					.append("isPackList").append(isPackList);
			log.error(errorMsg.toString(), e);
		}
		return result;
	}

	@GET
	@Path("/centerService/getWaybillDataAll/{waybillCode}")
	@GZIP
	public BaseEntity<BigWaybillDto> getWaybillDataAll(@PathParam("waybillCode") String waybillCode) {
		// 判断参数有效性
		if (StringHelper.isEmpty(waybillCode))
			return null;

		WChoice choice = new WChoice();
		choice.setQueryWaybillC(true);
		choice.setQueryWaybillE(true);
		choice.setQueryWaybillM(true);
		choice.setQueryPackList(true);
		choice.setQueryGoodList(true);
		choice.setQueryWaybillExtend(true);
		choice.setQueryPickupTask(true);
		choice.setQueryServiceBillPay(true);
		choice.setQueryQByNewCode(true);
		choice.setQueryWaybillFinance(true);
		choice.setQueryWaybillP(true);
		choice.setQueryWaybillS(true);
		choice.setQueryWaybillT(true);

		BaseEntity<BigWaybillDto> result = null;
		try {
			result = waybillQueryManager.getDataByChoice(waybillCode,choice);
		} catch (Exception e) {
			log.error("中心服务调用运单getDataByChoice出错", e);
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

	@GET
	@Path("/centerService/getPrintDataForCityOrder/{busiId}/{waybillCode}")
	@GZIP
	public PrintResultDTO getPrintDataForCityOrder(@PathParam("busiId") Integer busiId,
												   @PathParam("waybillCode") String waybillCode) {
		//调用外单接口，根据商家id获取商家编码
		String busiCode = "";
		BasicTraderInfoDTO basicTraderInfoDTO = baseMinorManager.getBaseTraderById(busiId);

		if(basicTraderInfoDTO != null){
			busiCode = basicTraderInfoDTO.getTraderCode();
		}
		if(StringHelper.isEmpty(busiCode)){
			return null;
		}
		WaybillPrintRequestDTO waybillPrintRequestDTO = new WaybillPrintRequestDTO();
		waybillPrintRequestDTO.setCustomerCode(busiCode);
		waybillPrintRequestDTO.setWaybillCode(waybillCode);
		return waybillPrintApi.getPrintDataForCityOrder(waybillPrintRequestDTO);
	}

	@GET
	@Path("/centerService/getPackageByWaybillCode/{waybillCode}")
	@GZIP
	public BaseEntity<List<DeliveryPackageD>> getPackageByWaybillCode(@PathParam("waybillCode") String waybillCode) {
		// 判断参数有效性
		if (StringHelper.isEmpty(waybillCode))
			return null;

		BaseEntity<List<DeliveryPackageD>> result = null;
		try {
			result = waybillPackageManager.getPackageByWaybillCode(waybillCode);
		} catch (Exception e) {
			log.error("中心服务调用运单getDataByChoice出错", e);
		}
		return result;
	}

	/**
	 * 调用ver的jsf接口，查询运单的路由
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/centerService/getRouterByWaybillCode/{waybillCode}")
	@GZIP
	public String getRouterByWaybillCode(@PathParam("waybillCode") String waybillCode){
		return jsfSortingResourceService.getRouterByWaybillCode(waybillCode);
	}
}
