package com.jd.bluedragon.distribution.arAbnormal;

import com.google.common.collect.Lists;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.common.utils.ProfilerHelper;
import com.jd.bluedragon.core.base.BaseMajorManager;
import com.jd.bluedragon.core.base.WaybillPackageManager;
import com.jd.bluedragon.core.base.WaybillQueryManager;
import com.jd.bluedragon.core.hint.constants.HintArgsConstants;
import com.jd.bluedragon.core.hint.constants.HintCodeConstants;
import com.jd.bluedragon.core.hint.service.HintService;
import com.jd.bluedragon.core.jmq.producer.DefaultJMQProducer;
import com.jd.bluedragon.distribution.api.JdResponse;
import com.jd.bluedragon.distribution.api.request.ArAbnormalRequest;
import com.jd.bluedragon.distribution.api.request.ArTransportModeChangeDto;
import com.jd.bluedragon.distribution.api.response.ArAbnormalResponse;
import com.jd.bluedragon.distribution.send.dao.SendDatailDao;
import com.jd.bluedragon.distribution.send.domain.SendDetail;
import com.jd.bluedragon.distribution.sorting.service.SortingService;
import com.jd.bluedragon.distribution.transport.domain.ArAbnormalReasonEnum;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReason;
import com.jd.bluedragon.distribution.transport.domain.ArContrabandReasonEnum;
import com.jd.bluedragon.distribution.transport.domain.ArTransportChangeModeEnum;
import com.jd.bluedragon.distribution.waybill.domain.WaybillStatus;
import com.jd.bluedragon.dms.job.JobHandler;
import com.jd.bluedragon.dms.job.JobResult;
import com.jd.bluedragon.dms.utils.BusinessUtil;
import com.jd.bluedragon.dms.utils.WaybillUtil;
import com.jd.bluedragon.domain.AreaNode;
import com.jd.bluedragon.utils.AreaHelper;
import com.jd.bluedragon.utils.BusinessHelper;
import com.jd.bluedragon.utils.DateHelper;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.etms.cache.util.EnumBusiCode;
import com.jd.etms.waybill.domain.BaseEntity;
import com.jd.etms.waybill.domain.DeliveryPackageD;
import com.jd.etms.waybill.domain.Waybill;
import com.jd.etms.waybill.dto.BdTraceDto;
import com.jd.etms.waybill.dto.BigWaybillDto;
import com.jd.etms.waybill.dto.WChoice;
import com.jd.jmq.common.exception.JMQException;
import com.jd.jmq.common.message.Message;
import com.jd.ql.basic.dto.BaseStaffSiteOrgDto;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * @author tangchunqing
 * @Description: 类描述信息
 * @date 2018年11月30日 15时:41分
 */
@Service("arAbnormalService")
public class ArAbnormalServiceImpl implements ArAbnormalService {
    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    @Qualifier("arAbnormalProducer")
    private DefaultJMQProducer arAbnormalProducer;

    @Autowired
    private WaybillQueryManager waybillQueryManager;

    @Autowired
    private WaybillPackageManager waybillPackageManager;

    @Autowired
    private SendDatailDao sendDatailDao;

    @Autowired
    @Qualifier("arTransportModeChangeProducer")
    private DefaultJMQProducer arTransportModeChangeProducer;

    @Autowired
    private BaseMajorManager baseMajorManager;
    
    @Autowired(required = false)
    @Qualifier("checkCanAirToRoadJobHandler")
    private JobHandler<List<String>,List<String>> checkCanAirToRoadJobHandler;
    
    @Value("${beans.ArAbnormalServiceImpl.checkCanAirToRoadTimeout:30000}")
    private long checkCanAirToRoadTimeout;
    
    @Autowired
    private SortingService sortingService;

    @Override
    public ArAbnormalResponse pushArAbnormal(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = validateRequest(arAbnormalRequest);
        //发mq
        if (ArAbnormalResponse.CODE_OK.equals(response.getCode())) {
            try {
                arAbnormalProducer.send(arAbnormalRequest.getPackageCode(), JsonHelper.toJson(arAbnormalRequest));
            } catch (JMQException e) {
                log.error("ArAbnormalServiceImpl.pushArAbnormal推送消息错误：{}" , JsonHelper.toJson(arAbnormalRequest), e);
                response.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
                response.setMessage(ArAbnormalResponse.MESSAGE_SERVICE_ERROR);
                addBusinessLog(arAbnormalRequest, response);
                return response;
            }
        }
        addBusinessLog(arAbnormalRequest, response);
        return response;
    }

    /**
     * 校验参数
     *
     * @param arAbnormalRequest
     * @return
     */
    private ArAbnormalResponse validateRequest(ArAbnormalRequest arAbnormalRequest) {
        ArAbnormalResponse response = new ArAbnormalResponse();
        if (StringUtils.isBlank(arAbnormalRequest.getPackageCode())) {
            response.setCode(ArAbnormalResponse.CODE_NODATA);
            response.setMessage(ArAbnormalResponse.MESSAGE_NODATA);
            return response;
        }
        arAbnormalRequest.setPackageCode(arAbnormalRequest.getPackageCode().trim());
        if (!WaybillUtil.isPackageCode(arAbnormalRequest.getPackageCode()) && !WaybillUtil.isWaybillCode(arAbnormalRequest.getPackageCode())
                && !BusinessUtil.isBoxcode(arAbnormalRequest.getPackageCode()) && !BusinessHelper.isSendCode(arAbnormalRequest.getPackageCode())) {
            response.setCode(ArAbnormalResponse.CODE_ERRORDATA);
            response.setMessage(ArAbnormalResponse.MESSAGE_ERRORDATA);
            return response;
        }
        if (ArAbnormalReasonEnum.getEnum(arAbnormalRequest.getTranspondReason()) == null) {
            response.setCode(ArAbnormalResponse.CODE_TRANSPONDREASON);
            response.setMessage(ArAbnormalResponse.MESSAGE_TRANSPONDREASON);
            return response;
        }
        if (ArTransportChangeModeEnum.getEnum(arAbnormalRequest.getTranspondType()) == null) {
            response.setCode(ArAbnormalResponse.CODE_TRANSPONDTYPE);
            response.setMessage(ArAbnormalResponse.MESSAGE_TRANSPONDTYPE);
            return response;
        }
        if (arAbnormalRequest.getSiteCode() == null) {
            response.setCode(ArAbnormalResponse.CODE_SITECODE);
            response.setMessage(ArAbnormalResponse.MESSAGE_SITECODE);
            return response;
        }
        if (StringUtils.isBlank(arAbnormalRequest.getSiteName())) {
            response.setCode(ArAbnormalResponse.CODE_SITENAME);
            response.setMessage(ArAbnormalResponse.MESSAGE_SITENAME);
            return response;
        }
        if (arAbnormalRequest.getUserCode() == null) {
            response.setCode(ArAbnormalResponse.CODE_USERCODE);
            response.setMessage(ArAbnormalResponse.MESSAGE_USERCODE);
            return response;
        }
        if (StringUtils.isBlank(arAbnormalRequest.getUserName())) {
            response.setCode(ArAbnormalResponse.CODE_USERNAME);
            response.setMessage(ArAbnormalResponse.MESSAGE_USERNAME);
            return response;
        }
        if (arAbnormalRequest.getOperateTime() != null) {
            try {
                DateHelper.parseAllFormatDateTime(arAbnormalRequest.getOperateTime());
            } catch (Exception e) {
                log.warn("ArAbnormalServiceImpl.pushArAbnormal时间格式错误:{}" , JsonHelper.toJson(arAbnormalRequest), e);
                response.setCode(ArAbnormalResponse.CODE_TIMEERROR);
                response.setMessage(ArAbnormalResponse.MESSAGE_TIMEERROR);
                return response;
            }
        } else {
            response.setCode(ArAbnormalResponse.CODE_TIME);
            response.setMessage(ArAbnormalResponse.MESSAGE_TIME);
            return response;
        }
        if(Objects.equals(arAbnormalRequest.getTranspondType(), ArTransportChangeModeEnum.AIR_TO_ROAD_CODE.getCode())
        		|| Objects.equals(arAbnormalRequest.getTranspondType(), ArTransportChangeModeEnum.AIR_TO_HIGH_SPEED_TRAIN_CODE.getCode())) {
        	ArAbnormalResponse result = checkCanAirToRoad(arAbnormalRequest);
        	if(!Objects.equals(ArAbnormalResponse.CODE_OK,result.getCode())) {
                response.setCode(result.getCode());
                response.setMessage(result.getMessage());
        		return response;
        	}
        	if(result != null && Boolean.TRUE.equals(result.getShowTipMsg())) {
        		response.setShowTipMsg(Boolean.TRUE);
        		response.setTipMsg(result.getTipMsg());
        	}
        }
        response.setCode(ArAbnormalResponse.CODE_OK);
        response.setMessage(ArAbnormalResponse.MESSAGE_OK);
        return response;
    }
    /**
     * 校验能否操作航空转陆运
     * @param arAbnormalRequest
     * @return
     */
    private ArAbnormalResponse checkCanAirToRoad(ArAbnormalRequest arAbnormalRequest) {
    	ArAbnormalResponse response = new ArAbnormalResponse();
        // 根据提报条码获取对应的运单和包裹信息
        Map<String, List<String>> waybillMap = getWaybillMapByBarCode(arAbnormalRequest);
        if(waybillMap == null
        		|| waybillMap.isEmpty()) {
            response.setCode(ArAbnormalResponse.CODE_OK);
            response.setMessage(ArAbnormalResponse.MESSAGE_OK);  
        	return response;
        }
        List<String> waybillCodes = Lists.newArrayList(waybillMap.keySet());
        List<String> checkFailWaybillCodes = null;
        CallerInfo call = ProfilerHelper.registerInfo("dmsWeb.job.checkCanAirToRoadJobHandler.handle");
        try {
        	JobResult<List<String>> jobResult= checkCanAirToRoadJobHandler.handle(waybillCodes, checkCanAirToRoadTimeout);
        	if(jobResult != null && jobResult.isSuc()) {
        		checkFailWaybillCodes = jobResult.getData();
        	}else {
                response.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
                response.setMessage("校验操作航空转陆运执行异常，请稍后重试！");
            	return response;
        	}
		} catch (Exception e) {
			Profiler.functionError(call);
			log.error("校验能否操作航空转陆运执行异常：请求参数：{}", JsonHelper.toJson(arAbnormalRequest),e);
            response.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
            response.setMessage("校验操作航空转陆运执行异常，请稍后重试！");
        	return response;
		}finally {
			Profiler.registerInfoEnd(call);
		}
        if(CollectionUtils.isEmpty(checkFailWaybillCodes)) {
            response.setCode(ArAbnormalResponse.CODE_OK);
            response.setMessage(ArAbnormalResponse.MESSAGE_OK);  
        	return response;
        }
        // 箱号、批次号，设置提醒信息
        if (BusinessHelper.isSendCode(arAbnormalRequest.getPackageCode())
             || BusinessUtil.isBoxcode(arAbnormalRequest.getPackageCode())) {
        	int failNum = 0;
        	int sucNum = 0;
        	for(String waybillCode : waybillMap.keySet()) {
        		List<String> packList = waybillMap.get(waybillCode);
        		if(packList == null) {
        			continue;
        		}
        		if(checkFailWaybillCodes.contains(waybillCode)) {
        			failNum += packList.size();
        		}else {
        			sucNum += packList.size();
        		}
        	}
            response.setCode(ArAbnormalResponse.CODE_OK);
            response.setMessage(ArAbnormalResponse.MESSAGE_OK); 
            response.setShowTipMsg(Boolean.TRUE);
            Map<String, String> hintParams = new HashMap<String, String>();
            hintParams.put(HintArgsConstants.ARG_FIRST, ""+sucNum);
            hintParams.put(HintArgsConstants.ARG_SECOND, ""+failNum);
            response.setTipMsg(HintService.getHint(HintCodeConstants.AIR_TO_ROAD_TIP_MSG,hintParams));
        } else {
            // 运单号、包裹号，不允许操作
            response.setCode(ArAbnormalResponse.CODE_SERVICE_ERROR);
            response.setMessage(HintService.getHint(HintCodeConstants.AIR_TO_ROAD_NOT_ALLOWD));
        }
		return response;
	}

	/**
     * 提报异常逻辑
     *
     * @param arAbnormalRequest
     */
    @JProfiler(jKey = "ArAbnormalServiceImpl.dealArAbnormal", jAppName = Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP, JProEnum.FunctionError})
    @Override
    public void dealArAbnormal(ArAbnormalRequest arAbnormalRequest) throws JMQException {
        // 根据提报条码获取对应的运单和包裹信息
        Map<String, List<String>> waybillMap = getWaybillMapByBarCode(arAbnormalRequest);
        //航空转陆运过滤非航空单
        if(Objects.equals(arAbnormalRequest.getTranspondType(), ArTransportChangeModeEnum.AIR_TO_ROAD_CODE.getCode())
        		|| Objects.equals(arAbnormalRequest.getTranspondType(), ArTransportChangeModeEnum.AIR_TO_HIGH_SPEED_TRAIN_CODE.getCode())) {
        	waybillMap = filterUnAirWaybill(waybillMap);
        }
        if (waybillMap != null) {
            // 发送全程跟踪
            this.doSendTrace(arAbnormalRequest, waybillMap);
            // 判断是否为新客户端请求
            if (isNewClientRequest(arAbnormalRequest)) {
                // 异常原因只有航空违禁品时，才发送MQ消息
                if (ArAbnormalReasonEnum.getEnum(arAbnormalRequest.getTranspondReason()) != ArAbnormalReasonEnum.CONTRABAND_GOODS) {
                    return;
                }
                // 第一期只做航空转陆运，其他运输类型不发送MQ消息 + 铁路转公路
                if (!(ArTransportChangeModeEnum.getEnum(arAbnormalRequest.getTranspondType()) == ArTransportChangeModeEnum.AIR_TO_ROAD_CODE
                        || ArTransportChangeModeEnum.getEnum(arAbnormalRequest.getTranspondType()) == ArTransportChangeModeEnum.RAILWAY_TO_ROAD_CODE)) {
                    return;
                }
                // 发MQ消息
                this.doSendMQ(arAbnormalRequest, waybillMap);
            }
        }
    }
    /**
     * 过滤掉非航空单
     * @param waybillMap
     * @return
     */
    private Map<String, List<String>> filterUnAirWaybill(Map<String, List<String>> waybillMap) {
    	if(CollectionUtils.isEmpty(waybillMap)) {
    		return waybillMap;
    	}
    	Map<String, List<String>> waybillMapNew = new HashMap<String, List<String>>();
		//过滤掉非航空单
    	for(String waybillCode : waybillMap.keySet()) {
			Waybill waybill = waybillQueryManager.queryWaybillByWaybillCode(waybillCode);
			if(waybill != null 
					&& !BusinessUtil.checkCanAirToRoad(waybill.getWaybillSign(), waybill.getSendPay())) {
				log.warn("航空转陆运处理，过滤掉非航空单：{}", waybillCode);
				continue;
			}
			waybillMapNew.put(waybillCode, waybillMap.get(waybillCode));
		}
		return waybillMapNew;
	}

	/**
     * 新老版本兼容，判断是否需要发送MQ消息
     * 新版空铁运输方式变更只处理航空转陆运，异常原因只有航空违禁品
     *
     * @param request
     * @return
     */
    private boolean isNewClientRequest(ArAbnormalRequest request) {
        // 新老版本客户端兼容，老客户端违禁品原因字段为空值，所以不需要发送MQ消息
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(request.getContrabandReason());
        if (contrabandReason == null) {
            return false;
        }
        return true;
    }

    /**
     * 根据条码类型 获取该条码下的运单-包裹关系对象
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapByBarCode(ArAbnormalRequest arAbnormalRequest) {
        String barCode = arAbnormalRequest.getPackageCode();
        if (BusinessHelper.isSendCode(barCode)) {
            // 批次号
            return this.getWaybillMapBySendCode(arAbnormalRequest);
        } else if (BusinessUtil.isBoxcode(barCode)) {
            // 箱号
            return this.getWaybillMapByBoxCode(arAbnormalRequest);
        } else if (WaybillUtil.isWaybillCode(barCode)) {
            // 运单号
            return this.getWaybillMapByWaybillCode(barCode);
        } else if (WaybillUtil.isPackageCode(barCode)) {
            // 包裹号
            return this.getWaybillMapByPackageCode(barCode);
        } else {
            log.warn("[空铁 - 运输方式变更]消费处理，无法识别条码，request:{}" , JsonHelper.toJson(arAbnormalRequest));
            return null;
        }
    }

    /**
     * 获取批次下的运单-包裹关系Map
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapBySendCode(ArAbnormalRequest arAbnormalRequest) {
        //按批次查出发货明细
        List<SendDetail> sendDetailList = sendDatailDao.queryWaybillsBySendCode(arAbnormalRequest.getPackageCode());
        if (sendDetailList.size() > 0) {
            return this.assembleBySendDetail(sendDetailList);
        } else {
            this.addErrorBusinessLog(arAbnormalRequest, "根据批次号没有发货明细");
        }
        return null;
    }

    /**
     * 获取箱号下的运单-包裹关系Map
     *
     * @param arAbnormalRequest
     * @return
     */
    private Map<String, List<String>> getWaybillMapByBoxCode(ArAbnormalRequest arAbnormalRequest) {
        //按箱号 和 站点查发货明细
        SendDetail queryParam = new SendDetail();
        queryParam.setBoxCode(arAbnormalRequest.getPackageCode());
        List<SendDetail> sendDetailList = sendDatailDao.querySendDatailsByBoxCode(queryParam);
        if (sendDetailList.size() > 0) {
            return this.assembleBySendDetail(sendDetailList);
        } else {
            this.addErrorBusinessLog(arAbnormalRequest, "箱号没有发货明细");
        }
        return null;
    }

    /**
     * 获取运单下的运单-包裹关系Map
     *
     * @param waybillCode
     * @return
     */
    private Map<String, List<String>> getWaybillMapByWaybillCode(String waybillCode) {
        BaseEntity<List<DeliveryPackageD>> baseEntity = waybillPackageManager.getPackListByWaybillCode(waybillCode);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            List<DeliveryPackageD> packageDList = baseEntity.getData();
            if (packageDList.size() > 0) {
                Map<String, List<String>> resultMap = new HashMap<>(1);
                resultMap.put(waybillCode, this.getPackageCodeStringList(packageDList));
                return resultMap;
            }
        }
        return null;
    }

    private List<String> getPackageCodeStringList(List<DeliveryPackageD> packageDList) {
        List<String> resultList = new ArrayList<>(packageDList.size());
        for (DeliveryPackageD packageD : packageDList) {
            resultList.add(packageD.getPackageBarcode());
        }
        return resultList;
    }

    private Map<String, List<String>> getWaybillMapByPackageCode(String packageCode) {
        Map<String, List<String>> resultMap = new HashMap<>(1);
        List packageList = new ArrayList(1);
        packageList.add(packageCode);
        resultMap.put(WaybillUtil.getWaybillCodeByPackCode(packageCode), packageList);
        return resultMap;
    }

    /**
     * 根据发货明细SendDetail 组装运单包裹关系对象
     *
     * @param sendDetails
     * @return
     */
    private Map<String, List<String>> assembleBySendDetail(List<SendDetail> sendDetails) {
        Map<String, List<String>> resultMap = new HashMap<>();
        for (SendDetail sendDetail : sendDetails) {
            if (resultMap.containsKey(sendDetail.getWaybillCode())) {
                resultMap.get(sendDetail.getWaybillCode()).add(sendDetail.getPackageBarcode());
            } else {
                List packageList = new ArrayList();
                packageList.add(sendDetail.getPackageBarcode());
                resultMap.put(sendDetail.getWaybillCode(), packageList);
            }
        }
        return resultMap;
    }

    /**
     * 组装并发送全程跟踪
     *
     * @param arAbnormalRequest
     * @param waybillMap
     */
    public void doSendTrace(ArAbnormalRequest arAbnormalRequest, Map<String, List<String>> waybillMap) {
        // 组装全程跟踪对象
        BdTraceDto bdTraceDto = assembleBdTraceDto(arAbnormalRequest);
        for (Map.Entry<String, List<String>> entry : waybillMap.entrySet()) {
            // 发送全程跟踪
            this.sendTrace(bdTraceDto, entry.getKey(), entry.getValue());
        }
    }

    /**
     * 封装全程跟踪对象
     *
     * @param arAbnormalRequest
     */
    private BdTraceDto assembleBdTraceDto(ArAbnormalRequest arAbnormalRequest) {
        BdTraceDto bdTraceDto = new BdTraceDto();
        bdTraceDto.setOperateType(WaybillStatus.WAYBILL_TRACK_ARQC);
        bdTraceDto.setOperatorSiteId(arAbnormalRequest.getSiteCode());
        bdTraceDto.setOperatorSiteName(arAbnormalRequest.getSiteName());
        bdTraceDto.setOperatorTime(DateHelper.parseAllFormatDateTime(arAbnormalRequest.getOperateTime()));
        bdTraceDto.setOperatorUserName(arAbnormalRequest.getUserName());
        bdTraceDto.setOperatorUserId(arAbnormalRequest.getUserCode());
        bdTraceDto.setOperatorDesp(this.getOperatorDesc(arAbnormalRequest));
        return bdTraceDto;
    }

    /**
     * 发全程跟踪
     *
     * @param bdTraceDto
     * @param waybillCode
     * @param packageCodeList
     */
    private void sendTrace(BdTraceDto bdTraceDto, String waybillCode, List<String> packageCodeList) {
        for (String packageCode : packageCodeList) {
            bdTraceDto.setWaybillCode(waybillCode);
            bdTraceDto.setPackageBarCode(packageCode);
            waybillQueryManager.sendBdTrace(bdTraceDto);
        }
    }

    private String getOperatorDesc(ArAbnormalRequest arAbnormalRequest) {
        StringBuilder desc = new StringBuilder();
        // 新旧版本客户端兼容问题
        if (isNewClientRequest(arAbnormalRequest)) {
            desc.append(getTransportChangeDesc(arAbnormalRequest.getTranspondType()));
            desc.append(Constants.SEPARATOR_BLANK_SPACE);
            desc.append(Constants.PUNCTUATION_OPEN_BRACKET_SMALL);
            desc.append(getContrabandReasonDesc(arAbnormalRequest.getContrabandReason()));
            desc.append(Constants.PUNCTUATION_CLOSE_BRACKET_SMALL);
        } else {
            desc.append(getAbnormalReasonDesc(arAbnormalRequest.getTranspondReason()));
            desc.append(Constants.SEPARATOR_BLANK_SPACE);
            desc.append(getTransportChangeDesc(arAbnormalRequest.getTranspondType()));
        }
        return desc.toString();
    }

    /**
     * 原因  代码和描述互转
     *
     * @param transpondReason
     * @return
     */
    private String getAbnormalReasonDesc(Integer transpondReason) {
        ArAbnormalReasonEnum reason = ArAbnormalReasonEnum.getEnum(transpondReason);
        if (reason != null) {
            return reason.getDesc();
        }
        return String.valueOf(transpondReason);
    }

    /**
     * 类型  代码和描述互转
     *
     * @param transpondType
     * @return
     */
    private String getTransportChangeDesc(Integer transpondType) {
        ArTransportChangeModeEnum changeMode = ArTransportChangeModeEnum.getEnum(transpondType);
        if (changeMode != null) {
            return changeMode.getDesc();
        }
        return String.valueOf(transpondType);
    }

    /**
     * 违禁品原因描述
     *
     * @param code
     * @return
     */
    private String getContrabandReasonDesc(Integer code) {
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(code);
        if (contrabandReason != null) {
            return contrabandReason.getDesc();
        }
        return String.valueOf(code);
    }

    /**
     * 添加出现异常情况下的PDA业务日志
     *
     * @param arAbnormalRequest
     * @param msg
     */
    private void addErrorBusinessLog(ArAbnormalRequest arAbnormalRequest, String msg) {
        ArAbnormalResponse response = new ArAbnormalResponse();
        response.setCode(JdResponse.CODE_SERVICE_ERROR);
        response.setMessage(msg);
        addBusinessLog(arAbnormalRequest, response);
    }

    /**
     * 插入PDA业务日志表
     *
     * @param arAbnormalRequest
     */
    private void addBusinessLog(ArAbnormalRequest arAbnormalRequest, ArAbnormalResponse arAbnormalResponse) {
        //写入自定义日志
        BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
        businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
        businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_ARABNORMAL);
        businessLogProfiler.setOperateType(Constants.BUSINESS_LOG_OPERATE_TYPE_ARABNORMAL);
        businessLogProfiler.setOperateRequest(JsonHelper.toJson(arAbnormalRequest));
        businessLogProfiler.setOperateResponse(JsonHelper.toJson(arAbnormalResponse));
        businessLogProfiler.setTimeStamp(System.currentTimeMillis());
        BusinessLogWriter.writeLog(businessLogProfiler);
    }

    /**
     * 发送MQ消息
     *
     * @param request
     * @param waybillMap
     */
    private void doSendMQ(ArAbnormalRequest request, Map<String, List<String>> waybillMap) throws JMQException {
        List<Message> messages = new ArrayList<>();
        for (Map.Entry<String, List<String>> entry : waybillMap.entrySet()) {
            BigWaybillDto bigWaybillDto = this.getBigWaybillDtoByWaybillCode(entry.getKey());
            if (bigWaybillDto != null && bigWaybillDto.getWaybill() != null) {
                //当运单为【航】字标的运单 || 京航达的运单 || 铁路转公路的运单时，发送MQ
                if (isNeedSendMQ(bigWaybillDto.getWaybill())
                        || Objects.equals(ArTransportChangeModeEnum.RAILWAY_TO_ROAD_CODE.getCode(), request.getTranspondType())) {
                    messages.addAll(this.assembleMessageList(request, bigWaybillDto, entry.getValue()));
                }
            } else {
                this.addErrorBusinessLog(request, "根据运单号：" + entry.getKey() + "获取运单信息为空");
            }
        }
        arTransportModeChangeProducer.batchSend(messages);
    }

    /**
     * 判断是否需要发消息
     *  1、【航】字标的运单， waybill_sign第31位等于1或84位等于3 才发消息
     *  2、京航达的运单，sendpday第137位等于1 发消息
     * @param waybill
     * @return
     */
    private boolean isNeedSendMQ(Waybill waybill) {
        return BusinessUtil.isArTransportMode(waybill.getWaybillSign()) || BusinessUtil.isJHD(waybill.getSendPay());
    }

    private BigWaybillDto getBigWaybillDtoByWaybillCode(String waybillCode) {
        WChoice choice = new WChoice();
        choice.setQueryWaybillC(true);
        choice.setQueryGoodList(true);
        choice.setQueryWaybillExtend(true);
        BaseEntity<BigWaybillDto> baseEntity = waybillQueryManager.getDataByChoice(waybillCode, choice);
        if (baseEntity.getResultCode() == EnumBusiCode.BUSI_SUCCESS.getCode() && baseEntity.getData() != null) {
            return baseEntity.getData();
        }
        return null;
    }

    /**
     * 组装MQ消息体List
     *
     * @param request
     * @param bigWaybillDto
     * @param packageCodeList
     * @return
     */
    private List<Message> assembleMessageList(ArAbnormalRequest request, BigWaybillDto bigWaybillDto, List<String> packageCodeList) {
        Waybill waybill = bigWaybillDto.getWaybill();
        ArTransportModeChangeDto dto = new ArTransportModeChangeDto();
        dto.setWaybillCode(waybill.getWaybillCode());
        dto.setOperatorErp(request.getUserErp());
        dto.setSiteCode(request.getSiteCode());

        // 原因处理
        dealWithReasons(request, dto);

        BaseStaffSiteOrgDto siteOrgDto = baseMajorManager.getBaseSiteBySiteId(request.getSiteCode());
        if (siteOrgDto != null) {
            dto.setSiteName(siteOrgDto.getSiteName());
            dto.setAreaId(siteOrgDto.getOrgId());
            AreaNode areaNode = AreaHelper.getArea(siteOrgDto.getOrgId());
            dto.setAreaName(areaNode == null ? null : areaNode.getName());
        }
        dto.setOperateTime(request.getOperateTime());
        // 青龙业主编码
        dto.setCustomerCode(waybill.getCustomerCode());
        // 青龙寄件人pin码
        dto.setConsignerPin(waybill.getMemberId());
        dto.setConsigner(waybill.getConsigner());
        dto.setConsignerId(waybill.getConsignerId());
        dto.setConsignerMobile(waybill.getConsignerMobile());
        dto.setPickupSiteId(waybill.getPickupSiteId());
        dto.setPickupSiteName(waybill.getPickupSiteName());
        // 商家ID
        dto.setBusiId(waybill.getBusiId());
        // 商家名称
        dto.setBusiName(waybill.getBusiName());
        dto.setConsignmentName(waybillQueryManager.getConsignmentNameByWaybillDto(bigWaybillDto));
        List<Message> messageList = new ArrayList<>(packageCodeList.size());
        for (String packageCode : packageCodeList) {
            dto.setPackageCode(packageCode);
            messageList.add(new Message(arTransportModeChangeProducer.getTopic(), JsonHelper.toJson(dto), packageCode));
        }
        return messageList;
    }

    /**
     * 一、二、三级原因处理
     * @param request
     * @return
     */
    private void dealWithReasons(ArAbnormalRequest request, ArTransportModeChangeDto dto) {
        ArTransportChangeModeEnum transformChangeMode = ArTransportChangeModeEnum.getEnum(request.getTranspondType());
        ArAbnormalReasonEnum abnormalReason = ArAbnormalReasonEnum.getEnum(request.getTranspondReason());
        ArContrabandReasonEnum contrabandReason = ArContrabandReasonEnum.getEnum(request.getContrabandReason());
        // 转发方式 (航空转陆运 或 航空转高铁)
        dto.setTransformType(transformChangeMode == null ? null :transformChangeMode.getFxmId());
        // 异常类型 (航空违禁品)
        dto.setAbnormalType(abnormalReason == null ? null : abnormalReason.getFxmId());
        // 转发方式 (航空转陆运 或 航空转高铁)
        dto.setFirstLevelCode(transformChangeMode == null ? null : transformChangeMode.getFxmId());
        dto.setFirstLevelName(transformChangeMode == null ? null : transformChangeMode.getDesc());

        // 航空转陆运 && 异常原因为违禁品 && 违禁品原因是带违禁品标识的 则将二级原因编码设置196，否则设置0
        if(Objects.equals(request.getTranspondType(), ArTransportChangeModeEnum.AIR_TO_ROAD_CODE.getCode())
                && ArContrabandReasonEnum.getContrabandFlagReason().contains(request.getContrabandReason())){
            dto.setSecondLevelCode(ArAbnormalReasonEnum.CONTRABAND_GOODS.getFxmId());
            dto.setSecondLevelName(ArAbnormalReasonEnum.CONTRABAND_GOODS.getDesc());
            dto.setThirdLevel(contrabandReason == null ? null : contrabandReason.getDesc());
        }else {
            dto.setAbnormalType(String.valueOf(Constants.NUMBER_ZERO));
            dto.setSecondLevelCode(String.valueOf(Constants.NUMBER_ZERO));
            dto.setSecondLevelName(abnormalReason == null ? null : abnormalReason.getDesc());
            dto.setThirdLevel(contrabandReason == null ? null : contrabandReason.getDesc());
        }
    }

    /**
     * 查询运输方式变更的原因
     *
     * @return
     */
    @Override
    public List<ArContrabandReason> getArContrabandReasonList() {
        List<ArContrabandReason> arContrabandReasons = new ArrayList<>();
        for (ArContrabandReasonEnum _enum : ArContrabandReasonEnum.values()) {
            ArContrabandReason arContrabandReason = new ArContrabandReason();
            arContrabandReason.setReasonCode(_enum.getCode());
            arContrabandReason.setReasonName(_enum.getDesc());
            arContrabandReasons.add(arContrabandReason);
        }
        return arContrabandReasons;
    }

    /**
     * 查询运输方式变更的原因-根据不同的运输方式变更类型获取不同的原因
     * @param transpondType
     * @return
     */
    @Override
    public List<ArContrabandReason> getArContrabandReasonListNew(Integer transpondType) {
        List<ArContrabandReason> arContrabandReasons = new ArrayList<>();
        for (ArContrabandReasonEnum _enum : ArContrabandReasonEnum.values()) {
            ArContrabandReason arContrabandReason = new ArContrabandReason();
            if(_enum.getTranspondTypes().contains(transpondType.toString())){
                arContrabandReason.setReasonCode(_enum.getCode());
                arContrabandReason.setReasonName(_enum.getDesc());
                arContrabandReasons.add(arContrabandReason);
            }
        }
        return arContrabandReasons;
    }
}