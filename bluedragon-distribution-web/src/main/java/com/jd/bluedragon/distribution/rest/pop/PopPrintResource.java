package com.jd.bluedragon.distribution.rest.pop;

import com.google.common.collect.Maps;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrintSmsMsg;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.task.domain.Task;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.*;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import java.util.Date;
import java.util.Map;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class PopPrintResource {

	private final Log logger = LogFactory.getLog(this.getClass());
	
	@Autowired
	private WaybillCommonService waybillCommonService;

	@Autowired
	private PopPrintService popPrintService;
	
	@Autowired
    private OperationLogService operationLogService;

	@Autowired
	private TaskService taskService;

	@Autowired
	private RedisManager redisManager;

	@Autowired
	private BaseMajorManager baseMajorManager;
	
    @Autowired
    @Qualifier("popPrintToSmsProducer")
    private DefaultJMQProducer popPrintToSmsProducer;

    @Autowired
    @Qualifier("zhuchangPrintToTerminalProducer")
    private DefaultJMQProducer zhuchangPrintToTerminalProducer;

	private static boolean isRedisModeAllowed = false;
	private static int PRINT_SOURCE = 3;  //发终端MQsource字段，标识数据来源（驻场打印：3）

	/**
	 * 根据运单号获取POP打印信息
	 * 
	 * @param waybillCode
	 * @return
	 */
	@GET
	@Path("/popPrint/findByWaybillCode/{waybillCode}")
	public PopPrintResponse findByWaybillCode(
			@PathParam("waybillCode") String waybillCode) {
		if (!WaybillUtil.isWaybillCode(waybillCode)) {
			this.logger.error("根据运单号“" + waybillCode + "”获取POP打印信息 --> 传入参数非法");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			PopPrint popPrint = this.popPrintService
					.findByWaybillCode(waybillCode);
			if (popPrint == null) {
				this.logger.info("根据运单号获取POP打印信息 --> 运单号：" + waybillCode
						+ ", 调用服务成功，数据为空");
				return new PopPrintResponse(PopPrintResponse.CODE_OK_NULL,
						PopPrintResponse.MESSAGE_OK_NULL);
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.logger.error("根据运单号“" + waybillCode + "”获取POP打印信息 --> 调用服务异常：", e);
			return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR,
					JdResponse.MESSAGE_SERVICE_ERROR);
		}
	}
	
	/**
	 * 保存POP打印信息
	 * 
	 * @param popPrintRequest
	 * @return
	 */
	@POST
	@Path("/popPrint/savePopPrint")
	public PopPrintResponse savePopPrint(PopPrintRequest popPrintRequest) {
		if (popPrintRequest == null
				|| !WaybillUtil.isWaybillCode(popPrintRequest.getWaybillCode())
				|| StringUtils.isBlank(popPrintRequest.getPackageBarcode())
				|| popPrintRequest.getOperateSiteCode() == null || popPrintRequest.getOperateSiteCode() == 0
				|| popPrintRequest.getOperatorCode() == null
				|| popPrintRequest.getOperateType() == null || popPrintRequest.getOperateType() == 0) {
			this.logger.error("保存POP打印信息savePopPrint --> 传入参数非法");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			/**
			 * 判断是否打印包裹
			 */
			boolean isPrintPack = PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType());
			// 验证运单号
			Waybill waybill = this.waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
			if (waybill == null) {
				this.logger.error("保存POP打印信息savePopPrint --> 运单【" + popPrintRequest.getWaybillCode() + "】不存在");
				return new PopPrintResponse(JdResponse.CODE_NO_POP_WAYBILL,
						JdResponse.MESSAGE_NO_POP_WAYBILL);
			}
			PopPrint popPrint = requestToPopPrint(popPrintRequest);
			
			int uptCount = 0;
			try {
				uptCount = this.popPrintService.updateByWaybillOrPack(popPrint);
			} catch (Exception e) {
				this.logger.info(
						"updateByWaybillOrPack失败" + popPrint == null ? null
								: popPrint.getWaybillCode(), e);
			}
			boolean savePopPrintToRedis = false;
			if (uptCount <= 0) {
				if (isPrintPack) {
					popPrint.setPrintCount(1);
				}
				if (isRedisModeAllowed) { // isRedisModeAllowed 开关控制是否存Redis
					savePopPrintToRedis = true;
				} else {
					try {
						popPrintService.add(popPrint);
					} catch (Exception e) {
						savePopPrintToRedis = true;// 数据库异常的时候存Redis
					}
				}
				if (savePopPrintToRedis) {
					savePopPrintToRedis(popPrint);
				}
				//推补发货任务
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrint.getOperateType())) {
					popPrintService.pushInspection(popPrint);
				}
				this.logger.info("插入POP打印信息savePopPrint成功，运单号【" + popPrint.getWaybillCode() + "】");
				if (isPrintPack) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "新增打印"));
					}
				}
			} else {
				this.logger.info("更新POP打印信息savePopPrint，运单号【" + popPrint.getWaybillCode() + "】");
				if (isPrintPack) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "更新打印"));
					}
				}
			}
			/**
			 * 需求：R2017M07N00868配送员接货，打印后发送mq信息给终端-sms
			 */
			boolean isNeedSendMQ = false;
			if(isPrintPack
					&&PopPrintRequest.POP_RECEIVE_TYPE_4.equals(popPrintRequest.getPopReceiveType())
					&&PopPrintRequest.BUS_TYPE_SITE_PLATFORM_PRINT.equals(popPrintRequest.getBusinessType())){
				isNeedSendMQ = true;
			}else if(isPrintPack
					&&PopPrintRequest.POP_RECEIVE_TYPE_5.equals(popPrintRequest.getPopReceiveType())
					&&PopPrintRequest.BUS_TYPE_IN_FACTORY_PRINT.equals(popPrintRequest.getBusinessType())
					){
				//驻厂打印 时
				if(StringUtils.isNotBlank(popPrintRequest.getBoxCode())){
					// 箱号不为空时 发送MQ
					isNeedSendMQ = true;
				}
				//操作站点类型 是站点的情况下 发送全程跟踪
				BaseStaffSiteOrgDto bDto = null;
				BaseStaffSiteOrgDto userDto=null;
				try {
					bDto = this.baseMajorManager.getBaseSiteBySiteId(popPrintRequest.getOperateSiteCode());
				} catch (Exception e) {
					logger.error("驻厂打印时获取站点失败 站点编号："+popPrintRequest.getOperateSiteCode()+"  "+e.getMessage());
				}
				try {
					userDto=baseMajorManager.getBaseStaffByStaffId(popPrintRequest.getOperatorCode());
				} catch (Exception e) {
					logger.error("获取操作人资料失败："+popPrintRequest.getOperatorCode()+"  "+e.getMessage());
				}

				if(bDto==null){
					logger.error("驻厂打印时获取站点为空 站点编号："+popPrintRequest.getOperateSiteCode());
				}else if (userDto==null){
					logger.error("获取操作人资料为空 id："+popPrintRequest.getOperatorCode());
				}else{
					if(BusinessHelper.isSiteType(bDto.getSiteType())){
						Date operatorTime=new Date(System.currentTimeMillis()-30000L);
						//操作站点类型符合 是站点
						toTask(popPrintRequest,WaybillStatus.WAYBILL_TRACK_UP_DELIVERY,"订单/包裹已接货",operatorTime);
						toTask(popPrintRequest,WaybillStatus.WAYBILL_TRACK_COMPLETE_DELIVERY,"配送员"+popPrintRequest.getOperatorName()+"揽收完成",operatorTime);
						//驻厂打印成功，发送mq给终端，他们去同步终端运单，避免挂单
						Map<String,Object> msgBody=Maps.newHashMap();
						msgBody.put("waybillCode",popPrintRequest.getWaybillCode());
						msgBody.put("packageCode",popPrintRequest.getPackageBarcode());
                        msgBody.put("goods",popPrintRequest.getCategoryName());
						msgBody.put("operatorErp",userDto.getErp());
						msgBody.put("operatorTime",operatorTime.getTime());
						msgBody.put("operatorSource",PRINT_SOURCE);
						zhuchangPrintToTerminalProducer.send(popPrintRequest.getWaybillCode(),JsonHelper.toJson(msgBody));
					}
				}


			}

			if(isNeedSendMQ){
				PopPrintSmsMsg popPrintSmsMsg = new PopPrintSmsMsg();
				BeanUtils.copyProperties(popPrint, popPrintSmsMsg);
				popPrintToSmsProducer.send(popPrintSmsMsg.getPackageBarcode(), JsonHelper.toJson(popPrintSmsMsg));
			}
			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.logger.error("保存POP打印信息savePopPrint --> 调用服务异常：", e);
			return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR,
					e.getMessage());
		}
	}
	/**
	 * 保存POP打印信息
	 * 
	 * @param popPrintRequest
	 * @return
	 */
	@POST
	@Path("/popPrint/savePopPrintNew")
	public PopPrintResponse savePopPrintNew(PopPrintRequest popPrintRequest) {
		if (popPrintRequest == null
				|| !WaybillUtil.isWaybillCode(popPrintRequest.getWaybillCode())
				|| popPrintRequest.getOperateSiteCode() == null || popPrintRequest.getOperateSiteCode() == 0
				|| popPrintRequest.getOperatorCode() == null
				|| popPrintRequest.getOperateType() == null || popPrintRequest.getOperateType() == 0) {
			this.logger.error("保存POP打印信息 --> 传入参数非法");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			// 验证运单号
			Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
			if (waybill == null || !(Constants.POP_LBP.equals(waybill.getType()) || Constants.POP_SOPL.equals(waybill.getType()))) {
				this.logger.error("保存POP打印信息 --> 运单【" + popPrintRequest.getWaybillCode() + "】不存在或者为非POP");
				return new PopPrintResponse(JdResponse.CODE_NO_POP_WAYBILL,
						JdResponse.MESSAGE_NO_POP_WAYBILL);
			}
			PopPrint popPrint = requestToPopPrint(popPrintRequest);
			
			int uptCount = 0;
			try{
				uptCount = popPrintService.updateByWaybillCode(popPrint);
			} catch (Exception e) {
				this.logger.info(
						"updateByWaybillOrPack失败" + popPrint == null ? null
								: popPrint.getWaybillCode(), e);
				// still keep uptCount default 0 
			}
			boolean savePopPrintToRedis = false;
			if (uptCount <= 0) {
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					popPrint.setPrintCount(1);
				}
				
				if (isRedisModeAllowed) { // isRedisModeAllowed 开关控制是否存Redis
					savePopPrintToRedis = true;
				} else {
					try {
						popPrintService.add(popPrint);
					} catch (Exception e) {
						savePopPrintToRedis = true;// 数据库异常的时候存Redis
					}
				}
				if (savePopPrintToRedis) {
					savePopPrintToRedis(popPrint);
				}
				//推补发货任务
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					popPrintService.pushInspection(popPrint);
				}
				this.logger.info("插入POP打印信息成功，运单号【" + popPrint.getWaybillCode() + "】");
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "新增打印"));
					}
				}
			} else {
				this.logger.info("更新POP打印信息，运单号【" + popPrint.getWaybillCode() + "】");
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "更新打印"));
					}
				}
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.logger.error("保存POP打印信息 --> 调用服务异常：", e);
			return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR,
					e.getMessage());
		}
	}
	
	private void savePopPrintToRedis(PopPrint popPrint) {
		Long result = redisManager.rpush(
				CacheKeyConstants.POP_PRINT_BACKUP_KEY, JsonUtil.getInstance()
						.object2Json(popPrint));
		if (result < 0) {
			logger.error("savePopPrintToRedis failed:" + JsonHelper.toJson(popPrint));
		}
	}
	
	@POST
	@Path("/popPrint/forceSavePopPrint/for")
	public PopPrintResponse forceSavePopPrint(PopPrintRequest popPrintRequest) {
		if (popPrintRequest == null
				|| !WaybillUtil.isWaybillCode(popPrintRequest.getWaybillCode())
				|| popPrintRequest.getOperateSiteCode() == null || popPrintRequest.getOperateSiteCode() == 0
				|| popPrintRequest.getOperatorCode() == null
				|| popPrintRequest.getOperateType() == null || popPrintRequest.getOperateType() == 0) {
			this.logger.error("保存POP打印信息 --> 传入参数非法--");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			// 验证运单号
			Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
			if (waybill == null || !(Constants.POP_LBP.equals(waybill.getType()) || Constants.POP_SOPL.equals(waybill.getType()))) {
				this.logger.error("保存POP打印信息 --> 运单【" + popPrintRequest.getWaybillCode() + "】不存在或者为非POP--");
				return new PopPrintResponse(JdResponse.CODE_NO_POP_WAYBILL,
						JdResponse.MESSAGE_NO_POP_WAYBILL);
			}
			PopPrint popPrint = forceRequestToPopPrint(popPrintRequest);
			
			int uptCount = popPrintService.updateByWaybillCode(popPrint);
			if (uptCount <= 0) {
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					popPrint.setPrintCount(1);
				}
				popPrintService.add(popPrint);
				//推补发货任务
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					popPrintService.pushInspection(popPrint);
				}
				this.logger.info("插入POP打印信息成功，运单号【" + popPrint.getWaybillCode() + "】--");
			} else {
				this.logger.info("更新POP打印信息，运单号【" + popPrint.getWaybillCode() + "】--");
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.logger.error("保存POP打印信息 --> 调用服务异常：--", e);
			return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR,
					e.getMessage());
		}
	}
	
	@GET
	@Path("/popPrint/redisMode/on")
	public String redisModeOn() {
		isRedisModeAllowed = true;
		return "Success";
	}

	@GET
	@Path("/popPrint/redisMode/off")
	public String redisModeOff() {
		isRedisModeAllowed = false;
		return "Success";
	}
	
	@GET
	@Path("/popPrint/redisMode/info")
	public String redisModeInfo() {
		if(isRedisModeAllowed){
			return "开";
		}else{
			return "关";
		}
	}
	
	@POST
	@Path("/popPrint/move2DB")
	public String moveRedisToDB() {
		try {
			long len = redisManager
					.llen(CacheKeyConstants.POP_PRINT_BACKUP_KEY);
			for (long i = 0; i < len; i++) {
				String popPrintStr = redisManager
						.lpop(CacheKeyConstants.POP_PRINT_BACKUP_KEY);
				PopPrint popPrint = com.jd.tbschedule.redis.utils.JsonUtil
						.jsonToBean(popPrintStr, PopPrint.class);
				popPrintService.add(popPrint);
			}
			if (len == 0) {
				return "Redis中没有数据，无需移动";
			}
		} catch (Exception e) {
			logger.error("moveRedisToDB failed", e);
			return "Failed";
		}
		return "Success";
	}
	
	@GET
	@Path("/popPrint/redis/len")
	public String getRedisLen() {
		try {
			long len = redisManager
					.llen(CacheKeyConstants.POP_PRINT_BACKUP_KEY);
			return String.valueOf(len);
		} catch (Exception e) {
			logger.error("moveRedisToDB failed", e);
			return "Failed";
		}
	}
	
	private PopPrint forceRequestToPopPrint(PopPrintRequest request) {
		PopPrint popPrint = new PopPrint();
		popPrint.setWaybillCode(request.getWaybillCode());
		if (StringUtils.isBlank(request.getPackageBarcode())) {
			popPrint.setPackageBarcode(request.getWaybillCode());
		} else {
			popPrint.setPackageBarcode(request.getPackageBarcode());
		}
		popPrint.setCreateSiteCode(request.getOperateSiteCode());
		popPrint.setCreateSiteName(request.getOperateSiteName());
		popPrint.setCreateUserCode(request.getOperatorCode());
		popPrint.setCreateUser(request.getOperatorName());
		popPrint.setPopSupId(request.getPopSupId());
		popPrint.setPopSupName(request.getPopSupName());
		popPrint.setQuantity(request.getQuantity());
		popPrint.setWaybillType(request.getWaybillType());
		popPrint.setCrossCode(request.getCrossCode());
		popPrint.setPopReceiveType(request.getPopReceiveType());
		popPrint.setThirdWaybillCode(request.getThirdWaybillCode());
		popPrint.setQueueNo(request.getQueueNo());
		popPrint.setOperateType(request.getOperateType());
		popPrint.setBoxCode(request.getBoxCode());
		popPrint.setDriverCode(request.getDriverCode());
		popPrint.setDriverName(request.getDriverName());
		
		popPrint.setBusiId(request.getBusiId());
		popPrint.setBusiName(request.getBusiName());
		popPrint.setInterfaceType(request.getInterfaceType());

		if (PopPrintRequest.PRINT_PACK_TYPE.equals(request.getOperateType())) {
			this.logger.info("--保存POP打印信息 -->  打印包裹：waybillCode: " + request.getWaybillCode() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
			popPrint.setPrintPackCode(request.getOperatorCode());
			popPrint.setPrintPackTime(DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT));
			popPrint.setPrintPackUser(request.getOperatorName());
		} else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
			this.logger.info("--保存POP打印信息 --> 打印发票：waybillCode: " + request.getWaybillCode() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
			popPrint.setPrintInvoiceCode(request.getOperatorCode());
			popPrint.setPrintInvoiceTime(DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT));
			popPrint.setPrintInvoiceUser(request.getOperatorName());
		} else {
			throw new RuntimeException("保存POP打印信息 --> 传入操作类型有误：" + request.getOperateType() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
		}
		return popPrint;
	}
	
	public PopPrint requestToPopPrint(PopPrintRequest request) {
		PopPrint popPrint = new PopPrint();
		popPrint.setWaybillCode(request.getWaybillCode());
		if (StringUtils.isBlank(request.getPackageBarcode())) {
			popPrint.setPackageBarcode(request.getWaybillCode());
		} else {
			popPrint.setPackageBarcode(request.getPackageBarcode());
		}
		popPrint.setCreateSiteCode(request.getOperateSiteCode());
		popPrint.setCreateSiteName(request.getOperateSiteName());
		popPrint.setCreateUserCode(request.getOperatorCode());
		popPrint.setCreateUser(request.getOperatorName());
		popPrint.setPopSupId(request.getPopSupId());
		popPrint.setPopSupName(request.getPopSupName());
		popPrint.setQuantity(request.getQuantity());
		popPrint.setWaybillType(request.getWaybillType());
		popPrint.setCrossCode(request.getCrossCode());
		popPrint.setPopReceiveType(request.getPopReceiveType());
		popPrint.setThirdWaybillCode(request.getThirdWaybillCode());
		popPrint.setQueueNo(request.getQueueNo());
		popPrint.setOperateType(request.getOperateType());
		popPrint.setBoxCode(request.getBoxCode());
		popPrint.setDriverCode(request.getDriverCode());
		popPrint.setDriverName(request.getDriverName());
		
		popPrint.setBusiId(request.getBusiId());
		popPrint.setBusiName(request.getBusiName());
		popPrint.setInterfaceType(request.getInterfaceType());

		popPrint.setCategoryName(request.getCategoryName());

		if (PopPrintRequest.PRINT_PACK_TYPE.equals(request.getOperateType())) {
			logger.info("保存POP打印信息 -->  打印包裹：waybillCode: " + request.getWaybillCode() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
			popPrint.setPrintPackCode(request.getOperatorCode());
			popPrint.setPrintPackTime(DateHelper.getSeverTime(request.getOperateTime()));
			popPrint.setPrintPackUser(request.getOperatorName());
		} else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
			logger.info("保存POP打印信息 --> 打印发票：waybillCode: " + request.getWaybillCode() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
			popPrint.setPrintInvoiceCode(request.getOperatorCode());
			popPrint.setPrintInvoiceTime(DateHelper.getSeverTime(request.getOperateTime()));
			popPrint.setPrintInvoiceUser(request.getOperatorName());
		} else {
			throw new RuntimeException("保存POP打印信息 --> 传入操作类型有误：" + request.getOperateType() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
		}
		return popPrint;
	}
	
	private OperationLog parseOperationLog(PopPrintRequest request, String remark) {
        OperationLog operationLog = new OperationLog();
        operationLog.setWaybillCode(request.getWaybillCode());
        operationLog.setCreateSiteCode(request.getOperateSiteCode());
        operationLog.setCreateSiteName(request.getOperateSiteName());
        operationLog.setCreateUserCode(request.getOperatorCode());
        operationLog.setCreateUser(request.getOperatorName());
		operationLog.setCreateTime(new Date());
        operationLog.setOperateTime(DateHelper.getSeverTime(request.getOperateTime()));
        operationLog.setLogType(OperationLog.LOG_TYPE_POP_PRINT);
        operationLog.setRemark(remark);
        return operationLog;
    }

    private void toTask(PopPrintRequest req,Integer operateType,String remark,Date date){
		WaybillStatus waybillStatus = new WaybillStatus();

		waybillStatus.setPackageCode(req.getPackageBarcode());
		waybillStatus.setWaybillCode(req.getWaybillCode());
		waybillStatus.setCreateSiteCode(req.getOperateSiteCode());
		waybillStatus.setCreateSiteName(req.getOperateSiteName());
		waybillStatus.setOperatorId(req.getOperatorCode());
		waybillStatus.setOperator(req.getOperatorName());
		waybillStatus.setOperateType(operateType);
		waybillStatus.setRemark(remark);
		waybillStatus.setOperateTime(date);

		String body = JsonHelper.toJson(waybillStatus);



		Task task = new Task();
		task.setType(Task.TASK_TYPE_WAYBILL_TRACK);
		task.setTableName(Task.getTableName(Task.TASK_TYPE_WAYBILL_TRACK));
		task.setSequenceName(Task.getSequenceName(task.getTableName()));
		task.setKeyword1(remark);
		task.setKeyword2(operateType.toString());
		task.setCreateSiteCode(req.getOperateSiteCode());
		task.setBody(body);
		task.setOwnSign(BusinessHelper.getOwnSign());

		taskService.add(task,true);  //直接创建task对象。因为taskService.toTask

	}


}
