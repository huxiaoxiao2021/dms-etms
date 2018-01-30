package com.jd.bluedragon.distribution.command.handler;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.handler.Handler;

/**
 * 
 * @ClassName: WaybillPrintHandler
 * @Description: 包裹标签打印处理接口
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:55:59
 *
 */
public class JsonCommandHandlerMapping<T, R> extends AbstractCommandHandlerMapping<String,T,R>{
	
	@Override
	public Handler<T, R> getHandler(JdCommand<String> k) {
		return this.handlerMap.get(k.getBusinessType());
	}
}
