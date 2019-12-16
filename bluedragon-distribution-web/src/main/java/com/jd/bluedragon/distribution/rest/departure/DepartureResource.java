package com.jd.bluedragon.distribution.rest.departure;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.ServiceMessage;
import com.jd.bluedragon.common.domain.ServiceResultEnum;
import com.jd.bluedragon.core.base.VosManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.*;
import com.jd.bluedragon.distribution.api.response.*;
import com.jd.bluedragon.distribution.base.service.BaseService;
import com.jd.bluedragon.distribution.departure.dao.DepartureTmpDao;
import com.jd.bluedragon.distribution.departure.domain.*;
import com.jd.bluedragon.distribution.departure.service.DepartureService;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.send.domain.SendM;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.StringHelper;
import com.jd.common.util.StringUtils;
import com.jd.etms.vos.dto.CarriagePlanDto;
import com.jd.etms.vos.dto.CommonDto;
import com.jd.etms.vos.dto.SendCarInfoDto;
import com.jd.etms.vos.dto.SendCarParamDto;
import com.jd.ql.basic.domain.BaseDataDict;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.jboss.resteasy.annotations.GZIP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.*;

@Controller
@Path(Constants.REST_URL)
@Consumes({ MediaType.APPLICATION_JSON })
@Produces({ MediaType.APPLICATION_JSON })
public class DepartureResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());

	@Autowired
	DepartureService departureService;
	@Autowired
	BaseService baseService;

	@Autowired
	VosManager vosManager;

	@Autowired
	DepartureTmpDao departureTmpDao;
	/**
	 *
	 * 方法描述 : 获取批次下包裹重量 项目名称： bluedragon-distribution-web 类名：
	 * DepartureResource.java 版本： v1.0 创建时间： 2012-12-5 下午4:51:22
	 *
	 * @param siteCode
	 *            站点编码
	 * @param sendCode
	 *            批次号
	 * @return SendMeasureResponse
	 */
	@GET
	@Path("/departure/sendmeasure/{siteCode}/{sendCode}")
	public SendMeasureResponse get(@PathParam("siteCode") Integer siteCode,
			@PathParam("sendCode") String sendCode) {

		SendMeasureResponse response = new SendMeasureResponse();
		response.setCode(SendMeasureResponse.CODE_OK);
		try {
			SendMeasure sendMeasure = this.departureService.getSendMeasure(
					siteCode, sendCode);
			if (sendMeasure == null) {
				response.setWeight(0D);
			} else {
				response.setWeight(sendMeasure.getWeight() == null ? Double
						.valueOf(0.0D)// FIXBUG: 0 ,会造成不必要的解封加封操作
						: sendMeasure.getWeight());
				response.setReceiveSiteCode(sendMeasure.getReceiveSiteCode());// 返回操作单位编码
			}
		} catch (Exception e) {
			response.setWeight(0d);
		}
		return response;
	}

	@GET
	@Path("/departure/sendbox/{boxCode}/{siteCode}")
	public SendBoxResponse getSendBoxInfo(@PathParam("boxCode") String boxCode,
			@PathParam("siteCode") Integer siteCode) {
		List<SendBoxDetailResponse> detail = new ArrayList<SendBoxDetailResponse>();
		List<SendBox> sendBoxs = this.departureService.getSendBoxInfo(boxCode,
				siteCode);
		if (sendBoxs != null) {
			for (SendBox sendBox : sendBoxs) {
				detail.add(this.toSendBoxResponse(sendBox));
			}
		}

		SendBoxResponse response = new SendBoxResponse();
		if (detail == null || detail.size() == 0) {
			response.setCode(SendBoxResponse.CODE_NOT_FOUND);
			response.setMessage("Not Found");
		} else {
			response.setCode(SendBoxResponse.CODE_OK);
			response.setDetail(detail);
		}
		return response;
	}

	/**
	 *
	 * 方法描述 : 检查批次状态 项目名称： bluedragon-distribution-web 类名：
	 * DepartureResource.java 创建时间： 2012-12-5 下午4:54:04
	 *
	 * @param siteCode
	 *            caozuo分拣中心编号
	 * @param sendCode
	 *            批次号
	 * @return JdResponse
	 */
	@GET
	@Path("/departure/checkSendStatus/{siteCode}/{sendCode}")
	public JdResponse checkSendStatus(@PathParam("siteCode") Integer siteCode,
			@PathParam("sendCode") String sendCode) {
		JdResponse response = new JdResponse();
		ServiceMessage<String> result;
		try {
			result = departureService.checkSendStatus(siteCode, sendCode);
			if (result.getResult().equals(ServiceResultEnum.SUCCESS)) {
				response.setCode(JdResponse.CODE_OK);
			} else if (result.getResult().equals(ServiceResultEnum.NOT_FOUND)) {
				response.setCode(JdResponse.CODE_NOT_FOUND);
				response.setMessage("查找不到该批次");
			} else if (result.getResult()
					.equals(ServiceResultEnum.WRONG_STATUS)) {
				response.setCode(JdResponse.CODE_WRONG_STATUS);
				response.setMessage("该批次已经被发车过");
			}
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_INTERNAL_ERROR);
			response.setMessage("查询失败");
		}
		return response;
	}

	@POST
	@Path("/departure/createDepartue")
	public JdResponse createDepartue(DepartureRequest request) {
		if (null == request) {
			log.warn("发车接口：/departure/createDepartue ,DepartureRequest为空");
			return new JdResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		log.debug("createDepartue received data: {}", request.toString());
		JdResponse response = new JdResponse();
		Departure departure = toDeparture(request);
		ServiceMessage<String> result;
		try {
			result = departureService.createDeparture(departure);
			if (result.getResult().equals(ServiceResultEnum.SUCCESS)) {
				response.setCode(JdResponse.CODE_OK);
				response.setMessage(JdResponse.MESSAGE_OK);
			} else {
				response.setCode(JdResponse.CODE_NOT_FOUND);
				response.setMessage(result.getErrorMsg());
			}
		} catch (Exception e) {
			response.setCode(JdResponse.CODE_NOT_FOUND);
			response.setMessage("生成批次失败");
			log.error("生成批次失败", e);
		}
		log.info("--Request end, /departure/createDepartue : {}", System.currentTimeMillis());
		return response;
	}

	private SendBoxDetailResponse toSendBoxResponse(SendBox data) {
		SendBoxDetailResponse response = new SendBoxDetailResponse();
		response.setBoxCode(data.getBoxCode());
		response.setSendCode(data.getSendCode());
		response.setSendTime(data.getSendTime());
		response.setSendUser(data.getSendUser());
		response.setWaybillCode(data.getWaybillCode());
		response.setPackagebarcode(data.getPackageBarcode());
		return response;
	}

	private Departure toDeparture(DepartureRequest request) {
		Departure departure = new Departure();
		List<SendM> sendMs = new ArrayList<SendM>();
		String carCode = null;
		Integer type = null;
		Integer businessType = null;

		Map<Integer, Object> recieveSitesmap = new HashMap<Integer, Object>();
		for (DepartureSendRequest departureSendRequest : request.getSends()) {

			// 如果为空批次，跳过
			if (StringUtils.isBlank(departureSendRequest.getSendCode()))
				continue;

			if (departureSendRequest.getReceiveSiteCode() != null&&(!departureSendRequest.getReceiveSiteCode() .equals(0))) {
				recieveSitesmap.put(departureSendRequest.getReceiveSiteCode(),
						null);
			}else{
				String sendCode=departureSendRequest.getSendCode();
				String[] sendCodeArray =sendCode.split("-");
				if(sendCodeArray.length==3){
					recieveSitesmap.put(Integer.parseInt(sendCodeArray[1]),
							null);
				}


			}

			SendM sendM = new SendM();


			sendM.setSendCode(departureSendRequest.getSendCode()); // 发货交接单号
			sendM.setThirdWaybillCode(departureSendRequest
					.getThirdWaybillCode()); // 发货运单号
			if (StringUtils.isNotEmpty(request.getCarCode())) {
				sendM.setCarCode(request.getCarCode()); // 车号
			} else {
				sendM.setCarCode(departureSendRequest.getCarCode()); // 车号
			}
			if (request.getUserCode() != null) {
				sendM.setUpdateUserCode(request.getUserCode()); // 操作人编码
			} else {
				sendM.setUpdateUserCode(departureSendRequest.getUserCode()); // 操作人编码
			}
			if (StringUtils.isNotEmpty(request.getUserName())) {
				sendM.setUpdaterUser(request.getUserName()); // 操作人姓名
			} else {
				sendM.setUpdaterUser(departureSendRequest.getUserName()); // 操作人姓名
			}
			if (departureSendRequest.getOperateTime() != null) {
				sendM.setUpdateTime(DateHelper
						.parseDateTime(departureSendRequest.getOperateTime())); // 操作时间
			} else {
				sendM.setUpdateTime(new Date()); // 操作时间
			}
			if (StringUtils.isNotEmpty(request.getSendUser())) {
				sendM.setSendUser(request.getSendUser()); // 司机
			} else {
				sendM.setSendUser(departureSendRequest.getSendUser()); // 司机
			}
			if (request.getBusinessType() != null) {
				sendM.setSendType(request.getBusinessType()); // 分拣发货类型
			} else {
				sendM.setSendType(departureSendRequest.getBusinessType()); // 分拣发货类型
			}
			if (request.getSiteCode() != null) {
				sendM.setCreateSiteCode(request.getSiteCode()); // 创建站点
			} else {
				sendM.setCreateSiteCode(departureSendRequest.getSiteCode()); // 创建站点
			}
			if (carCode == null) {
				if (StringUtils.isNotEmpty(request.getCarCode())) {
					carCode = request.getCarCode(); // 车号，发车表需要
				} else {
					carCode = departureSendRequest.getCarCode(); // 车号，发车表需要
				}
			}
			if (type == null) {
				if (null != request.getType()) {
					type = request.getType();
				} else {
					type = departureSendRequest.getType(); // 发车类型，发车表需要
				}
			}
			if (businessType == null) {
				if (request.getBusinessType() != null) {
					businessType = request.getBusinessType(); // 10自营20逆向30三方
				} else {
					businessType = departureSendRequest.getBusinessType(); // 10自营20逆向30三方
				}
			}
			Integer sendUserCode = 0;
			try {
				if (StringUtils.isNotEmpty(request.getSendUserCode())) {
					sendUserCode = Integer.parseInt(request.getSendUserCode());
				} else {
					sendUserCode = Integer.parseInt(departureSendRequest
							.getSendUserCode());
				}
			} catch (Exception e) {
				// avoid the sendUserCode from PDA can't be converted into
				// integer
				log.error("DepartureRequest sendUserCode[{}] can't be converted into integer",sendUserCode);
			}
			sendM.setSendUserCode(sendUserCode); // 司机编码
			sendM.setThirdWaybillCode(departureSendRequest
					.getThirdWaybillCode());
			//暂存发车的运力编码
			sendM.setTurnoverBoxCode(departureSendRequest.getCapacityCode());
			sendMs.add(sendM);
		}

		Set<Integer> recieveSitesSet = recieveSitesmap.keySet();

		StringBuilder recieveSiteCodesBuilder = new StringBuilder();
		for (Integer recieveSiteCode : recieveSitesSet) {
			recieveSiteCodesBuilder.append(recieveSiteCode).append(",");
		}

		String recieveSiteCodes = recieveSiteCodesBuilder.toString();
		if (recieveSiteCodes.endsWith(",")) {
			recieveSiteCodes = recieveSiteCodes.substring(0,
					recieveSiteCodes.length() - 1);
		}
		departure.setReceiveSiteCodes(recieveSiteCodes);
		departure.setVolume(request.getVolume());
		departure.setWeight(request.getWeight());
		if (type == null) {
			log.warn("发车接口：/departure/createDepartue in toDeparture error: type is null auto set value is 0");
			departure.setType(0);
		} else {
			departure.setType(type);
		}
		departure.setCarCode(carCode);
		departure.setSendUserType(request.getSendUserType());
		departure.setShieldsCarCode(request.getShieldsCarCode()); // 封车号
		departure.setSendMs(sendMs);
		departure.setOldCarCode(request.getOldCarCode());
		departure.setBusinessType(businessType);
		departure.setCapacityCode(request.getCapacityCode());
        departure.setRouteType(request.getRouteType());   // 区分干支线
		/**
		 * 班次
		 */

		if (request.getRunNumber() != null) {
			departure.setRunNumber(request.getRunNumber());

		}
		return departure;
	}

	/**
	 * 发车打印查询修改2014/5/13
	 */
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/departure/queryDeparture")
	public List queryDeparture(DeparturePrintRequest departurPrintRequest) {
		departurPrintRequest.setDepartType(3);
		List<DepartureCar> dataList = this.departureService
				.findDepartureList(departurPrintRequest);
		List<DeparturePrintResponse> responseList = new ArrayList<DeparturePrintResponse>();
		for (DepartureCar departureCar : dataList) {
			DeparturePrintResponse response = new DeparturePrintResponse();
			Integer createSiteCode = departureCar.getCreateSiteCode();
			BaseStaffSiteOrgDto site =this.baseService.getSiteBySiteID(createSiteCode);
			String createSiteName = site.getSiteName();
			if (createSiteName == null) {
				this.log.warn("发车打印根据始发站点编号{}获取的站点名称为空",createSiteCode);
			} else {
				response.setCreateSite(createSiteName);
			}
			response.setCreateTime(DateHelper.formatDate(
					departureCar.getCreateTime(), Constants.DATE_TIME_FORMAT));
			response.setPrintTime(DateHelper.formatDate(
					departureCar.getPrintTime(), Constants.DATE_TIME_FORMAT));
			response.setM3("");
			response.setShieldsCarCode(departureCar.getShieldsCarCode());
			response.setVolume(departureCar.getVolume());
			response.setWeight(departureCar.getWeight());
			response.setCarCode(departureCar.getCarCode());

			//运力类型,基础资料新维护属性
			//1自营2三方零担3三方整车4联营包车
			if (departureCar.getSendUserType() == null) {
				this.log.warn("发车分送人类型为空:{}" , departurPrintRequest);
			} else if (departureCar.getSendUserType().intValue() == 1) {// 司机
				if(departureCar.getCarCode()!=null){
				String sendUser = departureCar.getCarCode() + ","
						+ departureCar.getSendUser();
				response.setSendUser(sendUser);
				}else{
					response.setSendUser(departureCar.getSendUser());
				}
				// 三方承运商的承运人显示逻辑
			} else if (departureCar.getSendUserType().intValue() == 2
					||departureCar.getSendUserType().intValue() == 3
					||departureCar.getSendUserType().intValue() == 0
					||departureCar.getSendUserType().intValue() == 4) {
				String sendUser = departureCar.getSendUserCode() + ","
						+ departureCar.getSendUser();
				List<DepartureSend> departureSendList = this.departureService
						.getDepartureSendByCarId(departureCar.getShieldsCarId());
				Map<String, String> map = new HashMap<String, String>();
				for (DepartureSend departureSend : departureSendList) {
					map.put(departureSend.getThirdWaybillCode(), "");
				}
				//如果三方运单号为空则不显示
				if(map!=null && !map.isEmpty()){
					Set<String> set = map.keySet();
					StringBuilder sendUserBuilder = new StringBuilder(sendUser);
					for (String ss : set) {
						if(ss==null||ss.equals("")){
							continue;
						}
						sendUserBuilder.append(ss).append(",");
					}

					sendUser = sendUserBuilder.toString();
				}
				if (sendUser.endsWith(",")) {
					sendUser = sendUser.substring(0, sendUser.length() - 1);
				}
				response.setSendUser(sendUser);

			}

			if (StringUtils.isNotEmpty(departureCar.getReceiveSiteCodes())) {
				String receiveSiteCodes = departureCar.getReceiveSiteCodes();
				String[] receiveSiteCodesArray = receiveSiteCodes.split(",");

				StringBuffer receiveSitesBuffer = new StringBuffer();
				for (String siteCode : receiveSiteCodesArray) {
					String siteName = this.baseService
							.getSiteNameBySiteID(Integer.parseInt(siteCode));
					if (siteName != null) {
						receiveSitesBuffer.append(siteName).append(",");
					}
				}

				String receiveSites = receiveSitesBuffer.toString();
				if (receiveSites.endsWith(",")) {
					receiveSites = receiveSites.substring(0,
							receiveSites.length() - 1);
				}
				response.setReceiveSites(receiveSites);
			}

			if (departureCar.getRunNumber() != null) {
				List<BaseDataDict> runNumberList = this.baseService
						.getBaseDataDictList(6055, 2, 6055);
				for (BaseDataDict dict : runNumberList) {
					if (departureCar.getRunNumber().equals(dict.getTypeCode())) {
						response.setRunNumberName(dict.getTypeName());
						break;
					}
				}
			}
			response.setShieldsCarId(departureCar.getShieldsCarId());
			response.setCode(200);
			response.setMessage("");
			responseList.add(response);

		}

		return responseList;

	}

	/**
	 * 主要为测试wss接口调用
	 *
	 * @param type
	 * @param code
	 * @return
	 */
	@GET
	@Path("/departure/waybills/{type}/{code}")
	public List<SendDetail> getWaybillsByDeparture(
			@PathParam("type") Integer type, @PathParam("code") String code) {
		List<SendDetail> details = departureService.getWaybillsByDeparture(
				code, type);
		return details;
	}

	/**
	 *
	 * 方法描述 : 发车打印写入发车打印时间点
	 *
	 * @param departureCarId
	 * @return JdResponse
	 */
	@GET
	@Path("/departure/print/{departureCarId}")
	public JdResponse checkSendStatus(@PathParam("departureCarId") long departureCarId) {
		departureService.updatePrintTime(departureCarId);
		JdResponse response = new JdResponse(JdResponse.CODE_OK,JdResponse.MESSAGE_OK);
		return response;
	}


    /**
     *  根据订单号获取干线报损运输信息
     *  @param orderCode
     *  @return
     * */
    @GET
    @GZIP
    @Path("/departure/deliveryinfo/{orderCode}")
    public DepartureResponse deliveryInfo(@PathParam("orderCode") String orderCode){
        log.info("the ordercode is {}", orderCode);
		DepartureResponse dpResponse = new DepartureResponse();
		List<DeparturePrintResponse> departurePrintResponses = null;
        if(StringUtils.isEmpty(orderCode)){
            log.warn("获取干线报损运输信息失败，输入的订单号为空。");
            dpResponse.setCode(JdResponse.CODE_PARAM_ERROR);
            dpResponse.setMessage(JdResponse.MESSAGE_PARAM_ERROR);
            return dpResponse;
        }

		// 根据运单号获取发货信息，找出发货的批次
        try{
			departurePrintResponses = departureService.queryDeliveryInfoByOrderCode(orderCode);
        }catch(Exception e){
            log.error("获取干线报损运输信息失败，获取发货信息失败，原因 " , e);
            dpResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
            dpResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
            return dpResponse;
        }

        if(null == departurePrintResponses || departurePrintResponses.size() <= 0){
            dpResponse.setCode(DeparturePrintResponse.CODE_NULL_RESULT);
            dpResponse.setMessage(DeparturePrintResponse.MESSAGE_NULL_RESULT);
            return dpResponse;
        }

		List<String> sendCodes = new ArrayList<String>();

		for(DeparturePrintResponse response : departurePrintResponses){
			sendCodes.add(response.getSendCode());
		}

		// 根据发货的批次，找出相关的所有发车信息
		try{
			departurePrintResponses = departureService.queryDepartureInfoBySendCode(sendCodes);
		}catch(Exception ex){
			log.error("获取干线报损运输信息失败，获取发车信息失败，原因 ", ex);
			dpResponse.setCode(JdResponse.CODE_SERVICE_ERROR);
			dpResponse.setMessage(JdResponse.MESSAGE_SERVICE_ERROR);
			return dpResponse;
		}

		if(null == departurePrintResponses || departurePrintResponses.size() <= 0){
			dpResponse.setCode(DeparturePrintResponse.CODE_NULL_RESULT);
			dpResponse.setMessage(DeparturePrintResponse.MESSAGE_NULL_RESULT);
			return dpResponse;
		}

		for(DeparturePrintResponse response : departurePrintResponses){
			response.setWaybillCode(orderCode);
		}

		dpResponse.setDeliveryInfo(departurePrintResponses);
        dpResponse.setCode(JdResponse.CODE_OK);
        dpResponse.setMessage(JdResponse.MESSAGE_OK);
        return dpResponse;
    }


	/**
	 * 运输发车打印查询修改
	 */
	@SuppressWarnings("rawtypes")
	@POST
	@Path("/departure/queryCarryDeparture")
	public CarryDeparturePrintResponse queryCarryDeparture(CarryDeparturePrintRequest departurPrintRequest) {

		CarryDeparturePrintResponse response = new CarryDeparturePrintResponse();
		List<SendCarInfoDto> cars = Lists.newArrayList();

		try {
			if (departurPrintRequest == null) {
				log.warn("运输发车请求参数为空");
				response.setCode(1000);
				response.setMessage("运输发车请求参数为空");
				return response;
			}


			SendCarParamDto paramDto = new SendCarParamDto();
			paramDto.setBegSendCarTime(departurPrintRequest.getBegSendCarTime());
			paramDto.setCarLicense(departurPrintRequest.getCarLicense());
			paramDto.setCarrierCode(departurPrintRequest.getCarrierCode());
			paramDto.setEndSendCarTime(departurPrintRequest.getEndSendCarTIme());
			paramDto.setHandoverCode(departurPrintRequest.getHandoverCode());
			paramDto.setStartSiteCode(departurPrintRequest.getStartSiteCode());

			CommonDto<List<SendCarInfoDto>> repose = this.vosManager.getSendCar(paramDto);
			cars = repose.getData();
			if (cars == null) {
				log.info("请求服务成功，运输发车数据为空！");
				log.info(JsonHelper.toJson(paramDto));
				response.setCode(200);
				response.setMessage("请求服务成功，运输发车数据为空！");
			}
			else {
				log.info(JsonHelper.toJson(cars)+cars.size());
				response.setData(cars);
				response.setCode(200);
				response.setMessage("OK");
			}
		} catch (Exception e) {
			response.setMessage(e.getMessage());
			response.setCode(1000);
			log.error("运输发车打印：",e);
		}
		return response;
	}

	/**
	 *  发车支持200个批次
	 *  每次扫批次号都暂存在临时表中
	 *  @param request 发车相关参数
	 *  @return
	 * */
	@POST
	@GZIP
	@Path("/departure/departuresendtemp")
 	public DepartureTmpResponse departureSendTemp(DepartureTmpRequest request){
		if(null == request || StringHelper.isEmpty(request.getBatchCode())
				|| StringHelper.isEmpty(request.getSendCode())){
			log.warn("PDA 发车支持200批次接口调用，参数不正确");
			return new DepartureTmpResponse(DepartureTmpResponse.CODE_PARAM_ERROR,
					DepartureTmpResponse.MESSAGE_PARAM_ERROR);
		}

		try{
			departureTmpDao.insert(request);
		}catch (Exception ex){
			log.error("PDA 发车支持200批次保存临时表失败，原因 ", ex);
			return new DepartureTmpResponse(DepartureTmpResponse.CODE_SERVICE_ERROR,
					DepartureTmpResponse.MESSAGE_SERVICE_ERROR);
		}

		return new DepartureTmpResponse(DepartureTmpResponse.CODE_OK,
				DepartureTmpResponse.MESSAGE_OK);
	}
	/**
	 *  VOS根据运输计划号获取运输计划详情
	 *  @param carriagePlanCode
	 *  @return
	 * */
	@GET
	@GZIP
	@Path("/departure/queryCarriagePlanDetails/{carriagePlanCode}")
	@JProfiler(jKey = "DMS.DepartureResource.queryCarriagePlanDetails", mState = {JProEnum.TP, JProEnum.FunctionError})
	public DepartureCarriagePlanResponse queryCarriagePlanDetails(@PathParam("carriagePlanCode") String carriagePlanCode) throws Exception {
		DepartureCarriagePlanResponse departureCarriagePlanResponse = new DepartureCarriagePlanResponse();
		departureCarriagePlanResponse.setCode(DepartureCarriagePlanResponse.CODE_OK);

		CommonDto<CarriagePlanDto> repose;
		try {
			repose = this.vosManager.queryCarriagePlanDetails(carriagePlanCode);
		} catch (Exception ex) {
			log.error("调用运输接口异常queryCarriagePlanDetails:{}", carriagePlanCode,ex);
			throw new Exception("vosManager.queryCarriagePlanDetails jsf接口异常");
		}

		if (repose == null || repose.getData() == null) {
			departureCarriagePlanResponse.setCode(DepartureCarriagePlanResponse.CODE_OK_NULL);
			departureCarriagePlanResponse.setMessage(DepartureCarriagePlanResponse.MESSAGE_OK_NULL);
		} else {
			if (repose.getCode() == 0 || repose.getCode() == 2) {//运输返回的异常信息
				departureCarriagePlanResponse.setCode(repose.getCode());
				departureCarriagePlanResponse.setMessage(repose.getMessage());
			} else {
				CarriagePlanDto reposeData = repose.getData();
				departureCarriagePlanResponse.setCarriagePlanCode(reposeData.getCarriagePlanCode());//运输计划号
				departureCarriagePlanResponse.setOrderNum(reposeData.getOrderNum());//订单数
				departureCarriagePlanResponse.setTransMode(reposeData.getTransMode());
				departureCarriagePlanResponse.setTransmodeName(reposeData.getTransModeName());
				departureCarriagePlanResponse.setDriverName(reposeData.getDriverName());
				departureCarriagePlanResponse.setDrivingLicense(reposeData.getDrivingLicense());
				departureCarriagePlanResponse.setCarrierName(reposeData.getCarrierName());
				departureCarriagePlanResponse.setBeginSiteName(reposeData.getBeginSiteName());
				departureCarriagePlanResponse.setParkingSpaceNum(reposeData.getParkingSpaceNum());
			}
		}
		return departureCarriagePlanResponse;
	}

}
