package com.jd.bluedragon.distribution.rest.inspection;

import com.alibaba.fastjson.JSON;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintTrack;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.InspectionECRequest;
import com.jd.bluedragon.distribution.api.request.InspectionFCRequest;
import com.jd.bluedragon.distribution.api.request.InspectionRequest;
import com.jd.bluedragon.distribution.api.request.TurnoverBoxRequest;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.domain.InvokeResult;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.base.service.SiteService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.inspection.domain.Inspection;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.domain.InspectionPackProgress;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.InspectionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.inspection.service.impl.InspectionExceptionServiceImpl;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.distribution.router.RouterService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.domain.SortCrossDetail;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ql.basic.ws.BasicPrimaryWS;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * In order to inspection operation
 * 
 * @author wangzichen
 * 
 */
@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class InspectionResource {

	@Autowired
	InspectionExceptionService inspectionExceptionService;

	@Autowired
	WaybillPackageBarcodeService waybillPackageBarcodeService;

	@Autowired
	BaseService baseService;

	@Autowired
	BoxService boxService;

	@Autowired
	CenConfirmService cenConfirmService;

	@Autowired
	ReceiveService receiveService;


	@Autowired
	private InspectionService inspectionService;


	@Autowired
	private QueryGapTimeUtil queryGapTimeUtil;

	private final static Logger log = LoggerFactory.getLogger(InspectionResource.class);

	@POST
	@Path("/inspection/exceptionQueryExpand")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.inspectionExceptionByBoxOrThird", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InspectionECResponse inspectionExceptionByBoxOrThird(
			InspectionECRequest inspectionECRequest) {
		if (null == inspectionECRequest
				|| (StringUtils.isBlank(inspectionECRequest.getBoxCode()) && StringUtils
						.isBlank(inspectionECRequest.getPartnerIdOrCode()))) {
			log.info(" /inspection/exceptionQuery 参数错误或者参数不存在 ");
			return new InspectionECResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		} else if (StringUtils.isNotBlank(inspectionECRequest.getBoxCode())
				&& !BusinessHelper.isBoxcode(inspectionECRequest.getBoxCode())) {
			return new InspectionECResponse(
					InspectionECResponse.CODE_PARAM_BOX_ERROR,
					InspectionECResponse.MESSAGE_PARAM_BOX_ERROR);
		}
		return inspectionException(inspectionECRequest);
	}

	/**
	 * 分拣助手，异常查询：查询多验、少验验货异常信息
	 * 
	 * @param
	 * @return
	 */
	@POST
	@Path("/inspection/exceptionQuery")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.inspectionException", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InspectionECResponse inspectionException(
			InspectionECRequest inspectionECRequest) {
		if (null == inspectionECRequest) {
			log.info(" /inspection/exceptionQuery 参数错误或者参数不存在 ");
			return new InspectionECResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		InspectionEC inspectionEC = new InspectionEC();
		String partnerIdOrCode = inspectionECRequest.getPartnerIdOrCode();

		if (StringUtils.isBlank(partnerIdOrCode)
				&& StringUtils.isBlank(inspectionECRequest.getBoxCode())) {
			log.info(" /inspection/exceptionQuery 参数错误或者不存在, partnerIdOrCode : {}",partnerIdOrCode);
			return new InspectionECResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		List<InspectionEC> data;
		String emptyStr = " 无异常数据";
		try {
			BaseStaffSiteOrgDto base = null;

			// 判断一下是否为箱号
			if (StringUtils.isBlank(inspectionECRequest.getBoxCode())) {
				base = baseService.queryDmsBaseSiteByCode(partnerIdOrCode);// 调用基础资料接口通过站点id或者七位编码获取站点对象

				if (null == base
						|| null == base.getSiteCode()
						|| !Constants.BASE_SITE_TYPE_THIRD.equals(base
								.getSiteType())) {// base.getSiteType()：16为三方，4为自营
					log.info(" /inspection/exceptionQuery, 获取基础资料三方站点失败， partnerIdOrCode is: {}",partnerIdOrCode);
					return new InspectionECResponse(
							InspectionECResponse.CODE_PARAM_ERROR,
							InspectionECResponse.MESSAGE_PARAM_PARTNER_ERROR);
				}
				inspectionEC.setReceiveSiteCode(base.getSiteCode());
				emptyStr = "三方:" + partnerIdOrCode + emptyStr;
			} else {
				emptyStr = "箱子:" + inspectionECRequest.getBoxCode() + emptyStr;
			}

			inspectionEC.setCreateSiteCode(inspectionECRequest.getSiteCode());
			inspectionEC.setBoxCode(inspectionECRequest.getBoxCode());
			if (null != inspectionECRequest.getInspectionECType()) {
				inspectionEC.setInspectionECType(inspectionECRequest
						.getInspectionECType());
			}

			data = inspectionExceptionService.getByThird(inspectionEC);// 异常查询，根据三方code和异常类型(1少验
																		// ,2多验)
			List<PackageResponse> responses = new ArrayList<PackageResponse>();

			if (null == data || data.isEmpty()) {// 如果三方无异常数据时
				return new InspectionECResponse(
						InspectionECResponse.CODE_PARAM_ERROR, emptyStr);
			}
			for (InspectionEC bean : data) {

				PackageResponse packageResponse = new PackageResponse(
						bean.getWaybillCode(), bean.getPackageBarcode(),
						DateHelper.formatDateTime(bean.getCreateTime()));
				packageResponse.setBoxCode(bean.getBoxCode());
				packageResponse.setCreateSiteCode(bean.getCreateSiteCode());
				packageResponse.setReceiveSiteCode(bean.getReceiveSiteCode());
				packageResponse.setInspectionECType(bean.getInspectionECType());
				packageResponse.setReceiveSiteName(null != base ? base
						.getSiteName() : "");
				responses.add(packageResponse);
			}
			return new InspectionECResponse(JdResponse.CODE_OK,
					JdResponse.MESSAGE_OK, responses);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new InspectionECResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}

	}

	/**
	 * 第三方：异常处理：超区退回|多验退回|少验取消 OR 多验直接配送
	 * 
	 * @param
	 * @return
	 */
	@POST
	@Path("/inspection/exception/dispose")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.inspectionExceptionCancel", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResponse inspectionExceptionCancel(
			InspectionECRequest inspectionECRequest) {

		if (null == inspectionECRequest
				|| null == inspectionECRequest.getPackages()
				|| inspectionECRequest.getPackages().isEmpty()) {
			log.info(" web访问：/inspection/exception/dispose，json 转换为 InspectionRequest 异常 ");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		log.info(" web访问：/inspection/exception/dispose parameters: {}",inspectionECRequest.toString());

		if (inspectionECRequest.getPackages().size() > 200) {
			return new JdResponse(InspectionECResponse.CODE_PARAM_UPPER_LIMIT,
					InspectionECResponse.MESSAGE_PARAM_UPPER_LIMIT);
		}

		List<InspectionRequest> packages = inspectionECRequest.getPackages();// 需要退回或者取消的包裹

		List<InspectionEC> inspectionECs = new ArrayList<InspectionEC>();

		for (InspectionRequest requestBean : packages) {
			log.info("  /inspection/exception/dispose parameters of each package : {}",requestBean.toString());
			InspectionEC inspectionEC = InspectionEC
					.toInspectionEC(requestBean);
			// inspectionEC.setCreateSiteCode(inspectionECRequest.getSiteCode());
			inspectionEC.setCreateSiteCode(requestBean.getSiteCode());
			inspectionEC.setInspectionECType(inspectionECRequest
					.getInspectionECType());
			inspectionEC.setBoxCode(requestBean.getBoxCode());
			if (StringUtils.isNotBlank(requestBean.getBoxCode())) {
				Integer receiveSiteCode = this.getReceiveSiteCodeByBox(
						requestBean.getBoxCode(), requestBean.getSiteCode());
				if (null == receiveSiteCode || receiveSiteCode <= 0) {
					return new InspectionECResponse(
							InspectionECResponse.CODE_PARAM_BOX_INVALID,
							InspectionECResponse.MESSAGE_PARAM_BOX_INVALID);
				} else {
					inspectionEC.setReceiveSiteCode(receiveSiteCode);
				}
			} else {
				inspectionEC.setReceiveSiteCode(requestBean
						.getReceiveSiteCode());
			}
			inspectionEC.setCreateUser(requestBean.getUserName());
			inspectionEC.setCreateUserCode(requestBean.getUserCode());
			inspectionEC.setUpdateUser(requestBean.getUserName());
			inspectionEC.setUpdateUserCode(requestBean.getUserCode());
			inspectionECs.add(inspectionEC);
		}

		Collections.sort(inspectionECs);

		int result = 0;

		// 3:超区退回,4:多验退回,5:少验取消
		if (inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_OVER
				|| inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_SEND_BACK
				|| inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_CANCEL) {
			// 由于超区退回和多验退回是扫描的包裹，不是服务端带出来的，所以需要查询是否可以超区退回或者多验退回
			if (inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_OVER
					|| inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_SEND_BACK) {
				String disposeStatus = checkDispose(inspectionECs,
						inspectionECRequest.getOperationType());
				if (StringUtils.isNotBlank(disposeStatus)) {
					return new JdResponse(
							InspectionECResponse.CODE_DISPOSE_ERROR,
							disposeStatus);
				}
			}

			result = inspectionExceptionService.exceptionCancel(inspectionECs,
					inspectionECRequest.getOperationType());
		} else if (inspectionECRequest.getOperationType() == InspectionEC.INSPECTIONEC_TYPE_SEND) {// 6:多验直接配送
			result = inspectionExceptionService.directDistribution(
					inspectionECs, inspectionECRequest.getOperationType());
		} else {
			log.info(" web访问：/inspection/exception/dispose 参数operationType错误: {}",inspectionECRequest.getOperationType());
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		/* 当返回1000（不含）~2000（不含）时，为友好提示，并非异常，数据已经正确处理 */
		if (result > 0) {
			return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
		} else if (InspectionExceptionServiceImpl.PACKAGE_SENDED == result) {
			return new JdResponse(JdResponse.CODE_SENDED,
					JdResponse.MESSAGE_SENDED);
		} else {
			return new JdResponse(JdResponse.CODE_PACKAGE_ERROR,
					JdResponse.MESSAGE_PACKAGE_ERROR);
		}
	}

	private Integer getReceiveSiteCodeByBox(String boxCode, Integer siteCode) {
		Box box = new Box();
		box.setCode(boxCode);
		box.setCreateSiteCode(siteCode);
		Box selBox = boxService.findBoxByBoxCode(box);
		Integer receiveSiteCode = (null == selBox
				|| null == selBox.getReceiveSiteCode() || selBox
				.getReceiveSiteCode() < 0) ? Integer.valueOf(0) : selBox.getReceiveSiteCode();
		return receiveSiteCode;
	}

	private String checkDispose(List<InspectionEC> inspectionECs,
			Integer operationType) {
		return inspectionExceptionService.checkDispose(inspectionECs,
				operationType);

	}

	/**
	 * 通过运单号/包裹号获得运单信息及包裹信息
	 * 
	 * @param code
	 *            运单号或者包裹号
	 * @param
	 * @return
	 */
	@GET
	@Path("/inspection/getWaybillPackage/{packageOrWaybillCode}/{siteCode}/{receiveSiteCodeORBoxCode}")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.getWaybillPackage", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public WaybillResponse get(
			@PathParam("packageOrWaybillCode") String code,
			@PathParam("siteCode") Integer siteCode,
			@PathParam("receiveSiteCodeORBoxCode") String receiveSiteCodeORBoxCode) {
		Assert.notNull(code, "/inspection/getWaybillPackage/ 参数code为空");
		if (StringUtils.isBlank(code) || siteCode <= 0) {
			log.warn("包裹或者运单无数据：{}", code);
			return new WaybillResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		Integer receiveSiteCode;
		try {
			receiveSiteCode = 0;
			if (BusinessHelper.isBoxcode(receiveSiteCodeORBoxCode)) {
				receiveSiteCode = getReceiveSiteCodeByBox(
						receiveSiteCodeORBoxCode, siteCode);
			} else {
				receiveSiteCode = Integer.valueOf(receiveSiteCodeORBoxCode);
			}
			if (receiveSiteCode <= 0) {
				return new WaybillResponse(
						WaybillResponse.CODE_NO_RECEIVE_SITE,
						WaybillResponse.MESSAGE_NO_RECEIVE_SITE);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new WaybillResponse(WaybillResponse.CODE_NO_RECEIVE_SITE,
					WaybillResponse.MESSAGE_NO_RECEIVE_SITE);
		}

		WaybillResponse response = waybillPackageBarcodeService
				.getWaybillPackageBarcode(code, siteCode, receiveSiteCode);// 通过运单号获得包裹信息以及运单的客户地址
		if (null == response.getPackages() || response.getPackages().isEmpty()) {
			log.warn("运单接口返回包裹为空: {}", code);
			response.setCode(WaybillResponse.CODE_NO_DATA);
			response.setMessage(WaybillResponse.MESSAGE_NO_DATA);
		} else {
			if (null == response.getCode())
				response.setCode(JdResponse.CODE_OK);
			if (StringUtils.isBlank(response.getMessage()))
				response.setMessage(JdResponse.MESSAGE_OK);
		}
		return response;
	}

	@GET
	@Path("/inspection")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.inspection", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public HandoverResponse get(@QueryParam("type") String type,
			@QueryParam("createSiteCode") int createSiteCode,
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime) {

		HandoverResponse response = new HandoverResponse();
		CenConfirm queryParam = new CenConfirm();
		queryParam.setType(Short.valueOf(type));
		queryParam.setCreateSiteCode(createSiteCode);
		queryParam.setCreateTime(DateHelper.parseDateTime(startTime));// start
		queryParam.setInspectionTime(DateHelper.parseDateTime(endTime));// end

		if(!queryGapTimeUtil.checkPass(JsonHelper.toJson(queryParam),QueryGapTimeUtil.INSPECTION_RESOURCE_GET)){
			response.setCode(HandoverResponse.CODE_PARAM_ERROR);
			response.setMessage("您操作的太快了！稍作休息后再操作！");
			return response;
		}

		List<HandoverDetailResponse> data = new ArrayList<HandoverDetailResponse>();
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(createSiteCode);
		String siteName = (bDto == null ? "" : bDto.getSiteName());

		List<CenConfirm> cenConfirms = cenConfirmService
				.queryHandoverInfo(queryParam);
		if (cenConfirms != null && !cenConfirms.isEmpty()) {
			for (CenConfirm cenConfirm : cenConfirms) {
				data.add(this.toHandoverDetailResponse(cenConfirm, siteName,
						Integer.valueOf(type)));
			}
		}
		if (data.isEmpty()) {
			response.setCode(HandoverResponse.CODE_OK);
		} else {
			response.setCode(HandoverResponse.CODE_OK);
			response.setData(data);
		}
		return response;
	}

	private HandoverDetailResponse toHandoverDetailResponse(
			CenConfirm cenConfirm, String siteName, int type) {
		HandoverDetailResponse handoverDetailResponse = new HandoverDetailResponse();
		if (Constants.BUSSINESS_TYPE_FC == type) {
			handoverDetailResponse.setCreateSiteCode(cenConfirm
					.getReceiveSiteCode());// 库房编号
		} else {
			handoverDetailResponse.setCreateSiteCode(cenConfirm
					.getCreateSiteCode());
		}
		handoverDetailResponse.setCreateSiteName(siteName);
		handoverDetailResponse.setInspectionTime(cenConfirm.getOperateTime());
		handoverDetailResponse
				.setInspectionUser(cenConfirm.getInspectionUser());
		handoverDetailResponse.setInspectionUserCode(cenConfirm
				.getInspectionUserCode());
		handoverDetailResponse.setPackageCode(cenConfirm.getPackageBarcode());
		handoverDetailResponse.setWaybillCode(cenConfirm.getWaybillCode());
		return handoverDetailResponse;
	}

	@POST
	@Path("/inspection/returnWarehouse")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.getReturnWarehouse", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public HandoverResponse getReturnWarehouse(
			InspectionFCRequest inspectionFCRequest) {
		List<HandoverDetailResponse> data = new ArrayList<HandoverDetailResponse>();
		HandoverResponse response = new HandoverResponse();
		CenConfirm queryParam = new CenConfirm();
		queryParam.setType(Short.valueOf(inspectionFCRequest.getType()));
		queryParam.setReceiveSiteCode(inspectionFCRequest.getReceiveSiteCode());
		queryParam.setCreateTime(DateHelper.parseDateTime(inspectionFCRequest
				.getStartTime()));// start
		queryParam.setInspectionTime(DateHelper
				.parseDateTime(inspectionFCRequest.getEndTime()));// end
		queryParam.setWaybillCode(inspectionFCRequest.getWaybillCode());
		queryParam.setCreateSiteCode(inspectionFCRequest.getSiteCode());

		if(!queryGapTimeUtil.checkPass(JsonHelper.toJson(queryParam),QueryGapTimeUtil.INSPECTION_RESOURCE_RETURN_WAREHOUSE)){
			response.setCode(HandoverResponse.CODE_PARAM_ERROR);
			response.setMessage("您操作的太快了！稍作休息后再操作！");
			return response;
		}

		List<CenConfirm> cenConfirms = cenConfirmService
				.queryHandoverInfo(queryParam);
		if (cenConfirms != null && !cenConfirms.isEmpty()) {
			for (CenConfirm cenConfirm : cenConfirms) {
				data.add(this.toHandoverDetailResponse(cenConfirm, null,
						Integer.valueOf(inspectionFCRequest.getType())));
			}
		}
		if (data.isEmpty()) {
			response.setCode(HandoverResponse.CODE_OK);
		} else {
			response.setCode(HandoverResponse.CODE_OK);
			response.setData(data);
		}
		return response;
	}

	@POST
	@Path("/inspection/turnoverBox")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.turnoverBox", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public JdResponse turnoverBox(TurnoverBoxRequest turnoverBoxRequest) {
		if (null == turnoverBoxRequest
				|| turnoverBoxRequest.getBusinessType() == null) {
			log.info(" web访问：/inspection/turnoverBox，json 转换为 turnoverBoxRequest 异常 ");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		log.info(" web访问：/inspection/turnoverBox parameters: {}", turnoverBoxRequest.toString());
		/**
		 * 1.收 2.发 3.取消
		 */
		if (2 == turnoverBoxRequest.getBusinessType().intValue()) {
			List<String> turnoverBoxCodes = turnoverBoxRequest
					.getTurnoverBoxCodes();
			if (turnoverBoxCodes != null && !turnoverBoxCodes.isEmpty()) {
				for (String turnoverBoxCode : turnoverBoxCodes) {
					createTurnoverBox(turnoverBoxRequest, turnoverBoxCode);
				}
			} else {
				return new JdResponse(JdResponse.CODE_PARAM_ERROR,
						JdResponse.MESSAGE_PARAM_ERROR);
			}
		} else {
			createTurnoverBox(turnoverBoxRequest,
					turnoverBoxRequest.getTurnoverBoxCode());
		}
		return new JdResponse(JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
	}

	private void createTurnoverBox(TurnoverBoxRequest turnoverBoxRequest,
			String turnoverBoxCode) {
		TurnoverBox turnoverBox = new TurnoverBox();
		turnoverBox.setTurnoverBoxCode(turnoverBoxCode);
		turnoverBox.setCreateSiteCode(turnoverBoxRequest.getSiteCode());
		turnoverBox.setCreateSiteName(turnoverBoxRequest.getSiteName());
		turnoverBox.setCreateUser(turnoverBoxRequest.getUserName());
		turnoverBox.setCreateUserCode(turnoverBoxRequest.getUserCode());
		turnoverBox.setOperateTime(DateHelper.getSeverTime(turnoverBoxRequest
				.getOperateTime()));
		turnoverBox.setOperateType(turnoverBoxRequest.getBusinessType());
		turnoverBox.setReceiveSiteCode(turnoverBoxRequest.getReceiveSiteCode());
		turnoverBox.setReceiveSiteName(turnoverBoxRequest.getReceiveSiteName());
		try {
			receiveService.turnoverBoxAdd(turnoverBox);
		} catch (Exception ex) {
			log.error("web访问/inspection/turnoverBox异常:turnoverBoxAdd处理失败",ex);
		}
	}

	@GET
	@Path("/inspection/hintInfo/{packageOrWaybillCode}/{siteCode}")
	@JProfiler(jKey = "DMS.WEB.InspectionResource.getStorageCode", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public com.jd.ql.dms.common.domain.JdResponse getStorageCode(
			@PathParam("packageOrWaybillCode") String packageBarOrWaybillCode,
			@PathParam("siteCode") Integer siteCode){
		// 校验'验货'功能是否可用
		ImmutablePair<Boolean, String> checkResult = baseService.checkMenuIsAvailable(Constants.MENU_CODE_INSPECTION, siteCode);
		if(!checkResult.getLeft()){
			return new com.jd.ql.dms.common.domain.JdResponse(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL, checkResult.getRight());
		}
		return inspectionService.getStorageCode(packageBarOrWaybillCode,siteCode);
	}

	@GET
	@Path("/inspection/checkProgress/{packageOrWaybillCode}/{siteCode}")
	@JProfiler(jKey = "DMS.BASE.InspectionResource.getWaybillCheckPackDetail", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
	public InvokeResult<InspectionPackProgress> getWaybillCheckPackDetail(
			@PathParam("packageOrWaybillCode") String packageOrWaybillCode,
			@PathParam("siteCode") Integer siteCode) {

		InvokeResult<InspectionPackProgress> result = new InvokeResult<>();
		if (StringUtils.isBlank(packageOrWaybillCode)) {
			result.parameterError("运单号/包裹号为空！");
			return result;
		}

		try {
			String waybillCode = packageOrWaybillCode;
			if (WaybillUtil.isPackageCode(packageOrWaybillCode)) {
				waybillCode = WaybillUtil.getWaybillCode(packageOrWaybillCode);
			}
			result.setData(inspectionService.getWaybillCheckProgress(waybillCode, siteCode));
		}
		catch (Exception e) {
			result.error(e);
			log.error("Failed to get package check progress rate.[" + packageOrWaybillCode + "].", e);
		}

		return result;
	}


}
