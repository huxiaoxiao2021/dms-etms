package com.jd.bluedragon.distribution.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.command.handler.JsonCommandHandlerMapping;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.utils.JsonHelper;


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
	/**
	 * json格式的指令集配置
	 */
	@Autowired
	@Qualifier("jsonCommandHandlerMapping")
	private JsonCommandHandlerMapping<JdCommand<String>,JdResult<String>> JsonCommandHandlerMapping;
	
	@Override
	public String execute(String context) {
		JdCommand<String> jdCommand = JsonHelper.fromJson(context, JdCommand.class);
		//返回参数错误信息
		if(jdCommand == null){
			return JsonHelper.toJson(JdResults.REST_FAIL_PARAM_ERROR);
		}
		Handler<JdCommand<String>,JdResult<String>> handler = JsonCommandHandlerMapping.getHandler(jdCommand);
		//返回无服务信息
		if(handler == null){
			return JsonHelper.toJson(JdResults.REST_FAIL_SERVER_NOT_FIND);
		}else{
			return JsonHelper.toJson(handler.handle(jdCommand));
		}
	}
	
}
