package com.jd.bluedragon.distribution.handler;

/**
 * 
 * @ClassName: InterceptHandler
 * @Description: 拦截类型的处理逻辑接口
 * @author: wuyoude
 * @date: 2018年2月6日 上午9:20:17
 * 
 * @param <T>
 * @param <R>
 */
public interface InterceptHandler<T,R> extends Handler<T,InterceptResult<R>>{
	
}
