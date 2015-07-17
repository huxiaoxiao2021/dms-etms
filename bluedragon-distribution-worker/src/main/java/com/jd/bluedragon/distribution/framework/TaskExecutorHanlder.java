package com.jd.bluedragon.distribution.framework;

public interface TaskExecutorHanlder<T> {
	
	public boolean preHandle(T t);

	public boolean handleSuccess(T t) ;

	public boolean handleRedo(T t);

	public boolean handleError(T t);
}
