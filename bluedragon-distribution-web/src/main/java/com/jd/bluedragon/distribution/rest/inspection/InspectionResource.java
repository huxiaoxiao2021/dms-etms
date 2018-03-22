package com.jd.bluedragon.distribution.rest.inspection;

import java.util.*;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHint;
import com.jd.bluedragon.distribution.abnormal.domain.DmsOperateHintCondition;
import com.jd.bluedragon.distribution.abnormal.service.DmsOperateHintService;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.base.domain.DmsStorageArea;
import com.jd.bluedragon.distribution.base.service.DmsStorageAreaService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionResult;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.SerialRuleUtil;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.ql.dms.common.web.mvc.api.PagerResult;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.Assert;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.response.HandoverDetailResponse;
import com.jd.bluedragon.distribution.api.response.HandoverResponse;
import com.jd.bluedragon.distribution.api.response.InspectionECResponse;
import com.jd.bluedragon.distribution.api.response.PackageResponse;
import com.jd.bluedragon.distribution.api.response.WaybillResponse;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.box.domain.Box;
import com.jd.bluedragon.distribution.box.service.BoxService;
import com.jd.bluedragon.distribution.inspection.domain.InspectionEC;
import com.jd.bluedragon.distribution.inspection.service.InspectionExceptionService;
import com.jd.bluedragon.distribution.inspection.service.WaybillPackageBarcodeService;
import com.jd.bluedragon.distribution.inspection.service.impl.InspectionExceptionServiceImpl;
import com.jd.bluedragon.distribution.receive.domain.CenConfirm;
import com.jd.bluedragon.distribution.receive.domain.TurnoverBox;
import com.jd.bluedragon.distribution.receive.service.CenConfirmService;
import com.jd.bluedragon.distribution.receive.service.ReceiveService;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;

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
	private DmsStorageAreaService dmsStorageAreaService;

	@Autowired
	private WaybillQueryManager waybillQueryManager;

    @Autowired
    private DmsOperateHintService dmsOperateHintService;

	@Autowired
	private RedisManager redisManager;

	private final static Logger logger = Logger
			.getLogger(InspectionResource.class);

	@POST
	@Path("/inspection/exceptionQueryExpand")
	public InspectionECResponse inspectionExceptionByBoxOrThird(
			InspectionECRequest inspectionECRequest) {
		if (null == inspectionECRequest
				|| (StringUtils.isBlank(inspectionECRequest.getBoxCode()) && StringUtils
						.isBlank(inspectionECRequest.getPartnerIdOrCode()))) {
			logger.info(" /inspection/exceptionQuery 参数错误或者参数不存在 ");
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
	 * @param inspectionECRequest
	 *            传入第三方Code,异常类型
	 * @return
	 */
	@POST
	@Path("/inspection/exceptionQuery")
	public InspectionECResponse inspectionException(
			InspectionECRequest inspectionECRequest) {
		if (null == inspectionECRequest) {
			logger.info(" /inspection/exceptionQuery 参数错误或者参数不存在 ");
			return new InspectionECResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}

		InspectionEC inspectionEC = new InspectionEC();
		String partnerIdOrCode = inspectionECRequest.getPartnerIdOrCode();

		if (StringUtils.isBlank(partnerIdOrCode)
				&& StringUtils.isBlank(inspectionECRequest.getBoxCode())) {
			logger.info(" /inspection/exceptionQuery 参数错误或者不存在, partnerIdOrCode : "
					+ partnerIdOrCode);
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
					logger.info(" /inspection/exceptionQuery, 获取基础资料三方站点失败， partnerIdOrCode is: "
							+ partnerIdOrCode);
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
			logger.error(e.getMessage(), e);
			return new InspectionECResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}

	}

	/**
	 * 第三方：异常处理：超区退回|多验退回|少验取消 OR 多验直接配送
	 * 
	 * @param inspectionECRequest
	 * @return
	 */
	@POST
	@Path("/inspection/exception/dispose")
	public JdResponse inspectionExceptionCancel(
			InspectionECRequest inspectionECRequest) {

		if (null == inspectionECRequest
				|| null == inspectionECRequest.getPackages()
				|| inspectionECRequest.getPackages().isEmpty()) {
			logger.info(" web访问：/inspection/exception/dispose，json 转换为 InspectionRequest 异常 ");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		logger.info(" web访问：/inspection/exception/dispose parameters: "
				+ inspectionECRequest.toString());

		if (inspectionECRequest.getPackages().size() > 200) {
			return new JdResponse(InspectionECResponse.CODE_PARAM_UPPER_LIMIT,
					InspectionECResponse.MESSAGE_PARAM_UPPER_LIMIT);
		}

		List<InspectionRequest> packages = inspectionECRequest.getPackages();// 需要退回或者取消的包裹

		List<InspectionEC> inspectionECs = new ArrayList<InspectionEC>();

		for (InspectionRequest requestBean : packages) {
			logger.info("  /inspection/exception/dispose parameters of each package : "
					+ requestBean.toString());
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
			logger.info(" web访问：/inspection/exception/dispose 参数operationType错误: "
					+ inspectionECRequest.getOperationType());
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
	 * @param siteCode
     *           创建站点
	 * @return
	 */
	@GET
	@Path("/inspection/getWaybillPackage/{packageOrWaybillCode}/{siteCode}/{receiveSiteCodeORBoxCode}")
	public WaybillResponse get(
			@PathParam("packageOrWaybillCode") String code,
			@PathParam("siteCode") Integer siteCode,
			@PathParam("receiveSiteCodeORBoxCode") String receiveSiteCodeORBoxCode) {
		Assert.notNull(code, "/inspection/getWaybillPackage/ 参数code为空");
		if (StringUtils.isBlank(code) || siteCode <= 0) {
			logger.error("包裹或者运单无数据：" + code);
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
			logger.error(e.getMessage(), e);
			return new WaybillResponse(WaybillResponse.CODE_NO_RECEIVE_SITE,
					WaybillResponse.MESSAGE_NO_RECEIVE_SITE);
		}

		WaybillResponse response = waybillPackageBarcodeService
				.getWaybillPackageBarcode(code, siteCode, receiveSiteCode);// 通过运单号获得包裹信息以及运单的客户地址
		if (null == response.getPackages() || response.getPackages().isEmpty()) {
			logger.error("运单接口返回包裹为空: " + code);
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
	public HandoverResponse get(@QueryParam("type") String type,
			@QueryParam("createSiteCode") int createSiteCode,
			@QueryParam("startTime") String startTime,
			@QueryParam("endTime") String endTime) {
		List<HandoverDetailResponse> data = new ArrayList<HandoverDetailResponse>();
		BaseStaffSiteOrgDto bDto = baseService.getSiteBySiteID(createSiteCode);
		HandoverResponse response = new HandoverResponse();
		String siteName = (bDto == null ? "" : bDto.getSiteName());
		CenConfirm queryParam = new CenConfirm();
		queryParam.setType(Short.valueOf(type));
		queryParam.setCreateSiteCode(createSiteCode);
		queryParam.setCreateTime(DateHelper.parseDateTime(startTime));// start
		queryParam.setInspectionTime(DateHelper.parseDateTime(endTime));// end
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
	public JdResponse turnoverBox(TurnoverBoxRequest turnoverBoxRequest) {
		if (null == turnoverBoxRequest
				|| turnoverBoxRequest.getBusinessType() == null) {
			logger.info(" web访问：/inspection/turnoverBox，json 转换为 turnoverBoxRequest 异常 ");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		logger.info(" web访问：/inspection/turnoverBox parameters: "
				+ turnoverBoxRequest.toString());
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
			logger.error("web访问/inspection/turnoverBox异常:turnoverBoxAdd处理失败",
					ex);
		}
	}

	@GET
	@Path("/inspection/hintInfo/{packageOrWaybillCode}/{siteCode}")
    @JProfiler(jKey = "DMSWEB.REST.InspectionResource.getStorageCode",mState = {JProEnum.TP})
	public com.jd.ql.dms.common.domain.JdResponse getStorageCode(
			@PathParam("packageOrWaybillCode") String packageBarOrWaybillCode,
			@PathParam("siteCode") Integer siteCode){
		com.jd.ql.dms.common.domain.JdResponse jdResponse = new com.jd.ql.dms.common.domain.JdResponse();
		DmsStorageArea dmsStorageArea = new DmsStorageArea();
		//判断是运单号还是包裹号
		Integer dmsSiteCode = siteCode;
		String waybillCode = packageBarOrWaybillCode;
        if(BusinessHelper.isPackageCode(packageBarOrWaybillCode)){
            waybillCode = BusinessHelper.getWaybillCodeByPackageBarcode(packageBarOrWaybillCode);
        }
		InspectionResult inspectionResult = getInspectionResult(jdResponse, dmsStorageArea, dmsSiteCode, waybillCode);
        inspectionResult.setHintMessage(getHintMessage(waybillCode));
        jdResponse.toSucceed();//这里设置为成功，取不到值时记录warn日志
		jdResponse.setData(inspectionResult);
		return jdResponse;
	}

    /**
     * 根据运单号，取一个月内启用的运单提示语
     * 先从redis中查，查询异常再从DB查
     * @param waybillCode
     * @return
     */
    private String getHintMessage(String waybillCode){
        String hintMessage = "";
        try{
            hintMessage = redisManager.getCache(Constants.CACHE_KEY_PRE_PDA_HINT + waybillCode);
            logger.info("验货redis查询运单提示语，运单号：" + waybillCode + ",结果：" + hintMessage);
        }catch (Exception e){
            logger.error("验货redis查询运单提示语异常，改DB查询，运单号：" + waybillCode + "异常原因：" + e.getMessage());
        }
        return hintMessage;
    }

	/**
	 *  通过运单号获得库位号
	 * @param jdResponse
	 * @param dmsStorageArea
	 * @param dmsSiteCode 分拣中心ID
	 * @param waybillCode 运单号ID
	 * @return
	 * */
	private InspectionResult getInspectionResult(com.jd.ql.dms.common.domain.JdResponse jdResponse, DmsStorageArea dmsStorageArea, Integer dmsSiteCode, String waybillCode) {
		try{
			BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, true, false, false, false);
			if (baseEntity != null && baseEntity.getData() != null && baseEntity.getData().getWaybill() != null) {
				// 获取运单信息
				Waybill waybill = baseEntity.getData().getWaybill();
				dmsStorageArea.setDesProvinceCode(waybill.getProvinceId());
				dmsStorageArea.setDesCityCode(waybill.getCityId());
				dmsStorageArea.setDmsSiteCode(dmsSiteCode);
				DmsStorageArea newDmsStorageArea = dmsStorageAreaService.findByProAndCity(dmsStorageArea);
				if(newDmsStorageArea != null){
					String storageCode = newDmsStorageArea.getStorageCode();
					jdResponse.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_SUCCESS);
					return new InspectionResult(storageCode);
				}else {
					jdResponse.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL);
					jdResponse.setMessage("未找到对应的库位号");
					return new InspectionResult("");
				}
			}else{
				jdResponse.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL);
				jdResponse.setMessage("获取库位号失败");
				return new InspectionResult("");
			}
		}catch(Exception e){
			this.logger.warn("通过运单号获取库位号失败：" + waybillCode,e);
			jdResponse.setCode(com.jd.ql.dms.common.domain.JdResponse.CODE_FAIL);
			jdResponse.setMessage("获取库位号失败");
			return new InspectionResult("");
		}
	}
}
