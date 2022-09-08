package com.jd.bluedragon.distribution.command;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jd.bluedragon.Constants;
import com.jd.bluedragon.core.security.log.SecurityLogRecord;
import com.jd.bluedragon.core.security.log.domain.SecurityLogEntity;
import com.jd.bluedragon.core.security.log.enums.SecurityAccountEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogOpEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogReqInfoKeyEnums;
import com.jd.bluedragon.core.security.log.enums.SecurityLogUniqueIdentifierKeyEnums;
import com.jd.bluedragon.distribution.api.request.WaybillPrintRequest;
import com.jd.bluedragon.distribution.businessIntercept.constants.Constant;
import com.jd.bluedragon.distribution.businessIntercept.dto.SaveInterceptMsgDto;
import com.jd.bluedragon.distribution.businessIntercept.enums.BusinessInterceptOnlineStatusEnum;
import com.jd.bluedragon.distribution.businessIntercept.helper.BusinessInterceptConfigHelper;
import com.jd.bluedragon.distribution.businessIntercept.service.IBusinessInterceptReportService;
import com.jd.bluedragon.distribution.command.handler.JsonCommandHandlerMapping;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.print.domain.SurfaceOutputTypeEnum;
import com.jd.bluedragon.distribution.print.domain.WaybillPrintOperateTypeEnum;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.jsf.gd.util.RpcContext;
import com.jd.pfinder.profiler.sdk.trace.PFTracing;
import com.jd.ql.basic.util.DateUtil;
import com.jd.ql.dms.common.constants.OperateDeviceTypeConstants;
import com.jd.ql.dms.common.constants.OperateNodeConstants;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;
import com.jd.ump.profiler.CallerInfo;
import com.jd.ump.profiler.proxy.Profiler;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.*;


/**
 * 
 * @ClassName: JsonRequest
 * @Description: json请求
 * @author: wuyoude
 * @date: 2018年1月28日 下午1:05:28
 *
 */
@Service("jsonCommandService")
public class JsonCommandServiceImpl implements JdCommandService{
	private static final Logger log = LoggerFactory.getLogger(JsonCommandServiceImpl.class);

	private static Set<String> encryptedInfo = new HashSet<String>();
	static {
		encryptedInfo.add("customerName");//收件人姓名
		encryptedInfo.add("customerContacts");//收件人联系方式
		encryptedInfo.add("mobileFirst");//收件人手机前几位
		encryptedInfo.add("mobileLast");//收件人手机后几位
		encryptedInfo.add("telFirst");//收件人电话前几位
		encryptedInfo.add("telLast");//收件人电话后几位
		encryptedInfo.add("printAddress");//收件人地址

		encryptedInfo.add("consigner");//寄件人姓名
		encryptedInfo.add("consignerTel");//寄件人电话
		encryptedInfo.add("consignerMobile");//寄件人手机
		encryptedInfo.add("consignerAddress");//寄件人地址
		encryptedInfo.add("senderCompany");//寄件人公司
		encryptedInfo.add("consigneeCompany");//寄件人公司
	}

    // 拦截报表发送服务
    @Autowired
    private IBusinessInterceptReportService businessInterceptReportService;

    @Autowired
    private BusinessInterceptConfigHelper businessInterceptConfigHelper;

	/**
	 * json格式的指令集配置
	 */
	@Autowired
	@Qualifier("jsonCommandHandlerMapping")
	private JsonCommandHandlerMapping<JdCommand<String>,JdResult<String>> JsonCommandHandlerMapping;
	
	@SuppressWarnings("unchecked")
	@Override
	@PFTracing(name = "DMSWEB.JsonCommandServiceImpl.execute.Tracing")
	@JProfiler(jKey = "DMSWEB.JsonCommandServiceImpl.execute",jAppName=Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP,JProEnum.FunctionError})
	public String execute(String jsonCommand) {
		RpcContext context = RpcContext.getContext();
		String clientIp ="";
		if(context != null){
			clientIp =context.getRemoteHostName();
		}
		//设置初始化为成功
		JdResult<?> jdResult = new JdResult<String>(JdResult.CODE_SUC,200,"200-调用服务成功。");
		JdCommand<String> jdCommand = null;
		try {
			jdCommand = JsonHelper.fromJsonUseGson(jsonCommand, JdCommand.class);
		} catch (Exception e) {
			//json转换异常则返回参数错误信息
			log.error("JsonCommandServiceImpl.execute-params-error!params:{}",jsonCommand, e);
			jdResult = JdResults.REST_FAIL_PARAM_ERROR;
			jdCommand = new JdCommand<String>();
		}
		if(jdResult.isSucceed()){
			try {
				Handler<JdCommand<String>,JdResult<String>> handler = JsonCommandHandlerMapping.getHandler(jdCommand);
				if(handler == null){
					//返回无服务信息
					jdResult = JdResults.REST_FAIL_SERVER_NOT_FIND;
				}else{
					//正常处理
					jdResult = handler.handle(jdCommand);
				}
				// 收集拦截信息
			} catch (Exception e) {
				//处理异常返回异常信息
				log.error("JsonCommandServiceImpl.execute-error!", e);
				jdResult = JdResults.REST_ERROR_SERVER_EXCEPTION;
			}
		}
		String jsonResponse = JsonHelper.toJson(jdResult);
        this.saveInterceptLog(jdCommand, jdResult);
		//写入自定义日志
		writeBusinessLog(jsonCommand,jsonResponse,jdCommand.getOperateType());
		//写入安全日志
		writeSecurityLog(jdCommand, jsonResponse);
		return jsonResponse;
	}

	/**
	 * 写入操作日志
	 * @param jsonCommand
	 * @param responseJsonString
	 * @param operateType
	 */
	private void writeBusinessLog(String jsonCommand, String responseJsonString,Integer operateType){
		CallerInfo info = Profiler.registerInfo("DMSWEB.JsonCommandServiceImpl.writeBusinessLog", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try {
			JdResult<String> jdResultToLog = null;
			try {
				//写入自定义日志
				jdResultToLog = JSON.parseObject(responseJsonString, JdResult.class);

				if (jdResultToLog != null && jdResultToLog.getData() != null) {
					String dataStr = jdResultToLog.getData();
					if (StringUtils.isNotBlank(dataStr)) {
						JSONObject dataJson = JSON.parseObject(dataStr);
						String afterProcess = null;
						//平台打印、站点平台打印和其他打印逻辑返回值不一样，需要分开处理
						if (operateType.equals(WaybillPrintOperateTypeEnum.BATCH_SORT_WEIGH_PRINT.getType())
								|| operateType.equals(WaybillPrintOperateTypeEnum.SITE_PLATE_PRINT.getType())) {
							if (dataJson != null && dataJson.get("jsonData") != null) {
								String jsonDataStr = dataJson.get("jsonData").toString();
								dataJson.put("jsonData", encryptedInfoProcess(jsonDataStr));
								afterProcess = dataJson.toJSONString();
							}
						} else {
							afterProcess = encryptedInfoProcess(dataStr);
						}
						jdResultToLog.setData(afterProcess);
					}
				}
			}catch (Exception e){
				log.error("打印写操作日志异常.jsonCommand:{},responseJsonString:{},operateType:{}",jsonCommand, responseJsonString, operateType,e);
			}

			BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
			businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
			if (operateType == null) {
				operateType = Constants.OPERATE_TYPE_UNKNOWN;
			}
			businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_PRINT);
			businessLogProfiler.setOperateType(operateType);
			businessLogProfiler.setOperateRequest(jsonCommand);
			if(jdResultToLog!=null) {
				businessLogProfiler.setOperateResponse(JSON.toJSONString(jdResultToLog));
			}
			businessLogProfiler.setTimeStamp(System.currentTimeMillis());
			BusinessLogWriter.writeLog(businessLogProfiler);
		} finally {
			Profiler.registerInfoEnd(info);
		}
	}

	private void saveInterceptLog(JdCommand<String> jdCommand, JdResult<?> jdResult){
        try {
            if(jdResult.isSucceed()){
                // 无需拦截
                return;
            }
            //构建参数
            WaybillPrintRequest waybillPrintRequest = JsonHelper.fromJson(jdCommand.getData(), WaybillPrintRequest.class);
            if (null == waybillPrintRequest) {
                return;
            }
            //如果操作编码为空则设置为外层JdCommand的请求编码
            if(waybillPrintRequest.getBusinessType() == null){
                waybillPrintRequest.setBusinessType(jdCommand.getBusinessType());
            }
            if(waybillPrintRequest.getOperateType() == null){
                waybillPrintRequest.setOperateType(jdCommand.getOperateType());
            }
            if(StringUtils.isBlank(waybillPrintRequest.getVersionCode())){
                waybillPrintRequest.setVersionCode(jdCommand.getVersionCode());
            }

            SaveInterceptMsgDto saveInterceptMsgDto = new SaveInterceptMsgDto();
            saveInterceptMsgDto.setInterceptCode(jdResult.getMessageCode());
            saveInterceptMsgDto.setInterceptMessage(jdResult.getMessage());
            saveInterceptMsgDto.setBarCode(waybillPrintRequest.getBarCode());
            saveInterceptMsgDto.setSiteCode(waybillPrintRequest.getSiteCode());
            saveInterceptMsgDto.setDeviceType(businessInterceptConfigHelper.getOperateDeviceTypeByConstants(OperateDeviceTypeConstants.PRINT_CLIENT));
            saveInterceptMsgDto.setOperateNode(businessInterceptConfigHelper.getOperateNodeByConstants(OperateNodeConstants.PRINT));
            saveInterceptMsgDto.setDeviceCode(Constant.DEVICE_CODE_PRINT);
            long operateTimeMillis = System.currentTimeMillis();
            if(waybillPrintRequest.getOperateTime() != null){
                operateTimeMillis = DateUtil.parse(waybillPrintRequest.getOperateTime(), DateUtil.FORMAT_DATE_TIME).getTime();
            }
            saveInterceptMsgDto.setOperateTime(operateTimeMillis);
            saveInterceptMsgDto.setOperateSubNode(jdCommand.getOperateType());
            saveInterceptMsgDto.setSiteName(waybillPrintRequest.getSiteName());
            saveInterceptMsgDto.setOperateUserCode(waybillPrintRequest.getUserCode());
            saveInterceptMsgDto.setOperateUserErp(waybillPrintRequest.getUserERP());
            saveInterceptMsgDto.setOperateUserName(waybillPrintRequest.getUserName());
            saveInterceptMsgDto.setOnlineStatus(BusinessInterceptOnlineStatusEnum.ONLINE.getCode());

            try {
                businessInterceptReportService.sendInterceptMsg(saveInterceptMsgDto);
            } catch (Exception e) {
                String saveInterceptMqMsg = JSON.toJSONString(saveInterceptMsgDto);
                log.error("JsonCommandServiceImpl call sendInterceptMsg exception [{}]", saveInterceptMqMsg, e);
            }
        } catch (Exception e) {
            log.error("JsonCommandServiceImpl sendInterceptMsg exception [{}]" , e.getMessage(), e);
        }
    }

	/**
	 * 处理敏感信息
	 * 将涉及敏感信息的字段置为空
	 * @param data
	 * @return
	 */
	private String encryptedInfoProcess(String data){
		JSONObject jsonObject = JSON.parseObject(data);
		for(String fieldName : encryptedInfo){
			jsonObject.put(fieldName,null);
		}
		return jsonObject.toJSONString();
	}

	/**
	 * 写入安全日志
	 * @param jsonCommand
	 */
	private void writeSecurityLog(JdCommand<String> jsonCommand, String jsonResponse){
		CallerInfo info = Profiler.registerInfo("DMSWEB.JsonCommandServiceImpl.writeSecurityLog", Constants.UMP_APP_NAME_DMSWEB,false, true);
		try{
			Map<SecurityLogReqInfoKeyEnums, String> reqInfoKeyEnumsStringMap = new HashMap<>();
			reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.accountId, "data.userCode");
			reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.accountName, "data.userName");
			reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.carryBillId, "data.barCode");
			reqInfoKeyEnumsStringMap.put(SecurityLogReqInfoKeyEnums.inputParam, "data");

			Map<SecurityLogUniqueIdentifierKeyEnums, String> uniqueIdentifierKeyEnumsStringHashMap = new HashMap<>();
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.carryBillId,"data.waybillCode");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.receiveName,"data.customerName");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.receiveAddress,"data.printAddress");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.receivePhone,"data.customerContacts");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.senderPhone,"data.consignerTelText");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.senderAddress,"data.consignerAddress");
			uniqueIdentifierKeyEnumsStringHashMap.put(SecurityLogUniqueIdentifierKeyEnums.merchantCode,"data.waybillCode");

			SecurityLogRecord.log(
					SecurityLogEntity.builder()
							.interfaceName("com.jd.bluedragon.distribution.command.JsonCommandServiceImpl#execute")
							.accountName(String.valueOf(JsonHelper.getObject(JSONObject.parseObject(JSONObject.toJSONString(jsonCommand)),"data.userCode")))
							.accountType(SecurityAccountEnums.account_type_3)
							.op(
									SurfaceOutputTypeEnum.OUTPUT_TYPE_PRINT.getCode().equals(JsonHelper.getObject(JSONObject.parseObject(JSONObject.toJSONString(jsonCommand)),"data.outputType"))?
											SecurityLogOpEnums.op_8 : SecurityLogOpEnums.op_11
							)
							.reqKeyMapping(reqInfoKeyEnumsStringMap)
							.businessRequest(jsonCommand)
							.respKeyMapping(uniqueIdentifierKeyEnumsStringHashMap)
							.businessResponseList(Collections.<Object>singletonList(jsonResponse))
							.resultNum(1)
							.build()
			);
		}catch (Exception ex){
			log.error("构建安全日日志失败.jsonCommand:{}",jsonCommand,ex);
		}finally {
			Profiler.registerInfoEnd(info);
		}
	}
}
