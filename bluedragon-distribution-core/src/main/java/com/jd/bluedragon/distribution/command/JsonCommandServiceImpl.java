package com.jd.bluedragon.distribution.command;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.HandlerMappingFactory;
import com.jd.bluedragon.utils.JsonHelper;


/**
 * 
 * @ClassName: JsonRequest
 * @Description: json请求
 * @author: wuyoude
 * @date: 2018年1月28日 下午1:05:28
 *
 */
@Service("JsonCommandService")
public class JsonCommandServiceImpl implements JdCommandService{
	@Autowired
	private HandlerMappingFactory<JsonCommandHandlerMapping> handlerMappingFactory;
	
	@Override
	public String execute(String context) {
		JdCommand<String> jdCommand = JsonHelper.fromJson(context, JdCommand.class);
		JsonCommandHandlerMapping handlerMapping = handlerMappingFactory.getHandlerMapping(jdCommand);
		Handler handler = handlerMapping.getHandler(jdCommand.getOperateType());
		Object target = handlerMapping.parserCommand(jdCommand.getData());
		return JsonHelper.toJson(handler.handle(target));
	}
	
}
