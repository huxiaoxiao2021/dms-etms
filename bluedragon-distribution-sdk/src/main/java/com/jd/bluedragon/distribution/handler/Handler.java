package com.jd.bluedragon.distribution.handler;


/**
 * 
 * @ClassName: Handler
 * @Description: 处理逻辑单元接口
 * @author: wuyoude
 * @date: 2018年1月29日 上午10:28:37
 * 
 * @param <T> 处理目标实体
 * @param <R> 处理结果实体
 */
public interface Handler<T,R> {
	/**
	 * 执行处理，返回处理结果
	 * @param target
	 * @return
	 */
	R handle(T target);
}
