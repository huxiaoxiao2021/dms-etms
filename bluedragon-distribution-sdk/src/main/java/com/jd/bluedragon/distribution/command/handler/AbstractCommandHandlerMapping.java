package com.jd.bluedragon.distribution.command.handler;

import java.util.Map;

import com.jd.bluedragon.distribution.command.JdCommand;
import com.jd.bluedragon.distribution.handler.Handler;
import com.jd.bluedragon.distribution.handler.HandlerMapping;

/**
 * 
 * @ClassName: WaybillPrintHandler
 * @Description: 包裹标签打印处理接口
 * @author: wuyoude
 * @date: 2018年1月25日 下午10:55:59
 *
 */
public abstract class AbstractCommandHandlerMapping<C, T, R> implements HandlerMapping<JdCommand<C>,T,R>{
	/**
	 * 用于存放编码和处理逻辑的映射关系
	 */
	protected Map<Integer,Handler<T,R>> handlerMap;

	/**
	 * @return the handlerMap
	 */
	public Map<Integer, Handler<T, R>> getHandlerMap() {
		return handlerMap;
	}

	/**
	 * @param handlerMap the handlerMap to set
	 */
	public void setHandlerMap(Map<Integer, Handler<T, R>> handlerMap) {
		this.handlerMap = handlerMap;
	}
}
