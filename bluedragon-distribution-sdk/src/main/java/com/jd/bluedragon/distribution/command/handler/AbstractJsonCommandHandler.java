package com.jd.bluedragon.distribution.command.handler;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.command.JdResult;
import com.jd.bluedragon.distribution.command.JdResults;
import com.jd.bluedragon.distribution.handler.Handler;

public abstract class AbstractJsonCommandHandler<T,R> implements Handler<JdCommand<String>,JdResult<String>>{
	/**
	 * 逻辑单元映射关系
	 */
	private JsonOperateHandlerMapping<T,JdResult<String>> jsonOperateHandlerMapping;
	/**
	 * 将jsonData转换为处理逻辑需要的对象
	 * @param target 请求对象
	 * @return
	 */
	public abstract T fromJson(JdCommand<String> target);
	
	@Override
	public JdResult<String> handle(JdCommand<String> target) {
		Handler<T,JdResult<String>> handler = jsonOperateHandlerMapping.getHandler(target);
		//返回无服务信息
		if(handler == null){
			return JdResults.REST_FAIL_SERVER_NOT_FIND;
		}
		T target0 = fromJson(target);
		//返回参数错误信息
		if(target0 == null){
			return JdResults.REST_FAIL_PARAM_ERROR;
		}
		return handler.handle(target0);
	}

	/**
	 * @return the jsonOperateHandlerMapping
	 */
	public JsonOperateHandlerMapping<T, JdResult<String>> getJsonOperateHandlerMapping() {
		return jsonOperateHandlerMapping;
	}

	/**
	 * @param jsonOperateHandlerMapping the jsonOperateHandlerMapping to set
	 */
	public void setJsonOperateHandlerMapping(
			JsonOperateHandlerMapping<T, JdResult<String>> jsonOperateHandlerMapping) {
		this.jsonOperateHandlerMapping = jsonOperateHandlerMapping;
	}
}
