package com.jd.bluedragon.distribution.command;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.Constants;
import com.jd.bluedragon.distribution.command.handler.JsonCommandHandlerMapping;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.utils.JsonHelper;
import com.jd.dms.logger.aop.BusinessLogWriter;
import com.jd.dms.logger.external.BusinessLogProfiler;
import com.jd.ump.annotation.JProEnum;
import com.jd.ump.annotation.JProfiler;


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
	private static final Log logger= LogFactory.getLog(JsonCommandServiceImpl.class);
	/**
	 * json格式的指令集配置
	 */
	@Autowired
	@Qualifier("jsonCommandHandlerMapping")
	private JsonCommandHandlerMapping<JdCommand<String>,JdResult<String>> JsonCommandHandlerMapping;
	
	@SuppressWarnings("unchecked")
	@Override
	@JProfiler(jKey = "DMSWEB.JsonCommandServiceImpl.execute",jAppName=Constants.UMP_APP_NAME_DMSWEB, mState = {JProEnum.TP})
	public String execute(String jsonCommand) {
		//设置初始化为成功
		JdResult<?> jdResult = new JdResult<String>(JdResult.CODE_SUC,200,"200-调用服务成功。");
		JdCommand<String> jdCommand = null;
		try {
			jdCommand = JsonHelper.fromJsonUseGson(jsonCommand, JdCommand.class);
		} catch (Exception e) {
			//json转换异常则返回参数错误信息
			logger.error("JsonCommandServiceImpl.execute-params-error!params:"+jsonCommand, e);
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
			} catch (Exception e) {
				//处理异常返回异常信息
				logger.error("JsonCommandServiceImpl.execute-error!", e);
				jdResult = JdResults.REST_ERROR_SERVER_EXCEPTION;
			}
		}
		String jsonResponse = JsonHelper.toJson(jdResult);
		//写入自定义日志
		BusinessLogProfiler businessLogProfiler = new BusinessLogProfiler();
		businessLogProfiler.setSourceSys(Constants.BUSINESS_LOG_SOURCE_SYS_DMSWEB);
		Integer operateType = jdCommand.getOperateType();
		if(operateType == null){
			operateType = Constants.OPERATE_TYPE_UNKNOWN;
		}
		businessLogProfiler.setBizType(Constants.BUSINESS_LOG_BIZ_TYPE_PRINT);
		businessLogProfiler.setOperateType(operateType);
		businessLogProfiler.setOperateRequest(jsonCommand);
		businessLogProfiler.setOperateResponse(jsonResponse);
		businessLogProfiler.setTimeStamp(System.currentTimeMillis());
		BusinessLogWriter.writeLog(businessLogProfiler);
		return jsonResponse;
	}
}
