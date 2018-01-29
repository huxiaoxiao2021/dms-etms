package com.jd.bluedragon.distribution.handler;

/**
 * 
 * @ClassName: Handler
 * @Description: 处理逻辑单元接口
 * @author: wuyoude
 * @date: 2018年1月26日 上午12:12:11
 * 
 * @param <T> 处理上下文实体
 */
public abstract class InterceptHandler<T,R> implements Handler<T,R>{
	/**
	 * 处理逻辑单元
	 * @param context
	 */
	public abstract InterceptResult<R> preHandle(T context);
	/**
	 * 处理逻辑单元
	 * @param context
	 */
	public abstract InterceptResult<R> doHandle(T context);
	/**
	 * 处理逻辑单元
	 * @param context
	 */
	public abstract void afterHandle(T context);
	/**
	 * 1、先执行preHandle方法去验证是否通过
	 * 2、
	 */
	@Override
	public InterceptResult<R> handle(T context) {
		InterceptResult<R> interceptResult = this.preHandle(context);
		if(interceptResult != null && interceptResult.isPassed()){
			InterceptResult<R> result = this.doHandle(context);
			this.afterHandle(context);
			return result;
		}else{
			return interceptResult;
		}
	}
	
}
