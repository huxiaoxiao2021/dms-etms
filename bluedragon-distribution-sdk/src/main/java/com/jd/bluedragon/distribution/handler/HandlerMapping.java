package com.jd.bluedragon.distribution.handler;

/**
 * 
 * @ClassName: HandlerMapping
 * @Description: 处理逻辑映射关系接口
 * @author: wuyoude
 * @date: 2018年1月29日 上午10:29:26
 * 
 * @param <K> 
 * @param <T> 
 * @param <R>
 */
public interface HandlerMapping<K,T,R> {
	/**
	 * 处理逻辑单元
	 * @param context
	 */
	Handler<T,R> getHandler(K k);
}
