package com.jd.bluedragon.dms.job;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

/**
 * 任务处理器
 * @author wuyoude
 *
 * @param <J> 任务
 * @param <R> 任务处理结果
 */
public interface JobHandler<J,R>{
	/**
	 * 任务执行方法
	 * @param j
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	JobResult<R> handle(J j) throws InterruptedException, ExecutionException, TimeoutException;
	/**
	 * 任务执行方法
	 * @param j
	 * @param timeOut 超时时间（单位：毫秒）
	 * @return
	 * @throws InterruptedException
	 * @throws ExecutionException
	 * @throws TimeoutException
	 */
	JobResult<R> handle(J j,long timeOut) throws InterruptedException, ExecutionException, TimeoutException;
}
