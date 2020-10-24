package com.jd.bluedragon.distribution.rest.pop;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.domain.Waybill;
import com.jd.bluedragon.common.service.WaybillCommonService;
import com.jd.bluedragon.common.utils.CacheKeyConstants;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.redis.service.RedisManager;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.PopPrintRequest;
import com.jd.bluedragon.distribution.api.response.PopPrintResponse;
import com.jd.bluedragon.distribution.operationLog.domain.OperationLog;
import com.jd.bluedragon.distribution.operationLog.service.OperationLogService;
import com.jd.bluedragon.distribution.popPrint.domain.PopPrint;
import com.jd.bluedragon.distribution.popPrint.domain.ResidentTypeEnum;
import com.jd.bluedragon.distribution.popPrint.service.PopPrintService;
import com.jd.bluedragon.distribution.task.service.TaskService;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.bluedragon.utils.JsonUtil;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.Date;

@Component
@Path(Constants.REST_URL)
@Consumes( { MediaType.APPLICATION_JSON })
@Produces( { MediaType.APPLICATION_JSON })
public class PopPrintResource {

	private final Logger log = LoggerFactory.getLogger(this.getClass());
	
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
	
	private static boolean isRedisModeAllowed = false;

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
			this.log.warn("根据运单号{}获取POP打印信息 --> 传入参数非法",waybillCode);
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			PopPrint popPrint = this.popPrintService
					.findByWaybillCode(waybillCode);
			if (popPrint == null) {
				this.log.info("根据运单号获取POP打印信息 --> 运单号：{}, 调用服务成功，数据为空",waybillCode);
				return new PopPrintResponse(PopPrintResponse.CODE_OK_NULL,
						PopPrintResponse.MESSAGE_OK_NULL);
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.log.error("根据运单号{}获取POP打印信息 --> 调用服务异常：",waybillCode, e);
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
			this.log.warn("保存POP打印信息savePopPrint --> 传入参数非法");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
        return popPrintService.dealPopPrintLogic(popPrintRequest, ResidentTypeEnum.RESIDENT_DESK_CLIENT.getType());
	}
	/**
	 * 保存POP打印信息
	 * 
	 * @param popPrintRequest
	 * @return
	 */
	@POST
	@Path("/popPrint/savePopPrintNew")
    @JProfiler(jKey = "DMSWEB.PopPrintResource.savePopPrintNew",jAppName = Constants.UMP_APP_NAME_DMSWEB,
            mState = {JProEnum.TP, JProEnum.FunctionError})
	public PopPrintResponse savePopPrintNew(PopPrintRequest popPrintRequest) {
		if (popPrintRequest == null
				|| !WaybillUtil.isWaybillCode(popPrintRequest.getWaybillCode())
				|| popPrintRequest.getOperateSiteCode() == null || popPrintRequest.getOperateSiteCode() == 0
				|| popPrintRequest.getOperatorCode() == null
				|| popPrintRequest.getOperateType() == null || popPrintRequest.getOperateType() == 0) {
			this.log.warn("保存POP打印信息 --> 传入参数非法");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			// 验证运单号
			Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
			if (waybill == null || !(Constants.POP_LBP.equals(waybill.getType()) || Constants.POP_SOPL.equals(waybill.getType()))) {
				this.log.warn("保存POP打印信息 --> 运单【{}】不存在或者为非POP",popPrintRequest.getWaybillCode());
				return new PopPrintResponse(JdResponse.CODE_NO_POP_WAYBILL,
						JdResponse.MESSAGE_NO_POP_WAYBILL);
			}
			PopPrint popPrint = requestToPopPrint(popPrintRequest);
			
			int uptCount = 0;
			try{
				uptCount = popPrintService.updateByWaybillCode(popPrint);
			} catch (Exception e) {
				this.log.error("updateByWaybillOrPack失败", e);
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
				this.log.info("插入POP打印信息成功，运单号【{}】",popPrint.getWaybillCode());
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "新增打印","/popPrint/savePopPrint"));
					}
				}
			} else {
				this.log.info("更新POP打印信息，运单号【{}】",popPrint.getWaybillCode());
				if (PopPrintRequest.PRINT_PACK_TYPE.equals(popPrintRequest.getOperateType())) {
					if (!savePopPrintToRedis) {
						this.operationLogService.add(parseOperationLog(popPrintRequest, "更新打印","/popPrint/savePopPrint"));
					}
				}
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.log.error("保存POP打印信息 --> 调用服务异常：", e);
			return new PopPrintResponse(JdResponse.CODE_SERVICE_ERROR,
					e.getMessage());
		}
	}
	
	private void savePopPrintToRedis(PopPrint popPrint) {
		Long result = redisManager.rpush(
				CacheKeyConstants.POP_PRINT_BACKUP_KEY, JsonUtil.getInstance()
						.object2Json(popPrint));
		if (result < 0) {
			log.warn("savePopPrintToRedis failed:{}", JsonHelper.toJson(popPrint));
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
			this.log.warn("保存POP打印信息 --> 传入参数非法--");
			return new PopPrintResponse(JdResponse.CODE_PARAM_ERROR,
					JdResponse.MESSAGE_PARAM_ERROR);
		}
		try {
			// 验证运单号
			Waybill waybill = waybillCommonService.findByWaybillCode(popPrintRequest.getWaybillCode());
			if (waybill == null || !(Constants.POP_LBP.equals(waybill.getType()) || Constants.POP_SOPL.equals(waybill.getType()))) {
				this.log.warn("保存POP打印信息 --> 运单【{}】不存在或者为非POP--",popPrintRequest.getWaybillCode());
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
				this.log.info("插入POP打印信息成功，运单号【{}】--", popPrint.getWaybillCode());
			} else {
				this.log.info("更新POP打印信息，运单号【{}】--", popPrint.getWaybillCode());
			}

			PopPrintResponse popPrintResponse = new PopPrintResponse(
					JdResponse.CODE_OK, JdResponse.MESSAGE_OK);
			BeanUtils.copyProperties(popPrint, popPrintResponse);
			return popPrintResponse;
		} catch (Exception e) {
			this.log.error("保存POP打印信息 --> 调用服务异常：--", e);
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
			log.error("moveRedisToDB failed", e);
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
			log.error("moveRedisToDB failed", e);
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
			popPrint.setPrintPackCode(request.getOperatorCode());
			popPrint.setPrintPackTime(DateHelper.parseDate(request.getOperateTime(), Constants.DATE_TIME_FORMAT));
			popPrint.setPrintPackUser(request.getOperatorName());
		} else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
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
			popPrint.setPrintPackCode(request.getOperatorCode());
			popPrint.setPrintPackTime(DateHelper.getSeverTime(request.getOperateTime()));
			popPrint.setPrintPackUser(request.getOperatorName());
		} else if (PopPrintRequest.PRINT_INVOICE_TYPE.equals(request.getOperateType())) {
			popPrint.setPrintInvoiceCode(request.getOperatorCode());
			popPrint.setPrintInvoiceTime(DateHelper.getSeverTime(request.getOperateTime()));
			popPrint.setPrintInvoiceUser(request.getOperatorName());
		} else {
			throw new RuntimeException("保存POP打印信息 --> 传入操作类型有误：" + request.getOperateType() + ", 操作人：" + request.getOperatorCode() + ", 操作时间：" + request.getOperateTime());
		}
		return popPrint;
	}
	
	private OperationLog parseOperationLog(PopPrintRequest request, String remark,String url) {
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
        operationLog.setUrl(url);
        return operationLog;
    }

}
