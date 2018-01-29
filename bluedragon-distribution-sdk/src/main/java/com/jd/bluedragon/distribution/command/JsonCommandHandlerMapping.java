package com.jd.bluedragon.distribution.command;

import com.jd.bluedragon.distribution.handler.HandlerMapping;



/**
 * 
 * @ClassName: Handler
 * @Description: 处理逻辑单元接口
 * @author: wuyoude
 * @date: 2018年1月26日 上午12:12:11
 * 
 * @param <T> 处理上下文实体
 */
public interface JsonCommandHandlerMapping<K,T,R> extends HandlerMapping<K,T,R>{
	T parserCommand(String jsonStr);
}
